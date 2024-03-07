package com.wen.singlelogin.manage;

import com.wen.singlelogin.model.User;
import com.wen.singlelogin.model.UserLoginRedisInfo;
import com.wen.singlelogin.utils.IpUtil;
import com.wen.singlelogin.utils.RedisKeyUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

import static com.wen.singlelogin.constant.RedisKeyConstant.*;

/**
 * 用户登录实现类
 *
 * @author wen
 */
@Component
@Slf4j
public class SessionManage{

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.session.timeout}")
    private long sessionTimeout;

    /**
     * 设置属性
     *
     * @param request 请求信息
     * @param key 键
     * @param value 值
     * @param isLogin 是否登录
     */
    public void setAttribute(HttpServletRequest request, String key, Object value, boolean isLogin) {
        HttpSession session = request.getSession();
        if (isLogin) {
            UserLoginRedisInfo userLoginRedisInfo = (UserLoginRedisInfo) value;
            User user = userLoginRedisInfo.getUser();
            // 存储登录态
            session.setAttribute(key, user);
            // 存储 Session 和 IP 信息
            String sessionId = session.getId();
            String userExtraInfoKey = RedisKeyUtil.getUserExtraInfoKey(user.getId());
            stringRedisTemplate.opsForHash().put(userExtraInfoKey, SESSION_ID, sessionId);
            stringRedisTemplate.opsForHash().put(userExtraInfoKey, IP, userLoginRedisInfo.getIp());
            stringRedisTemplate.expire(userExtraInfoKey, sessionTimeout, TimeUnit.SECONDS);
        }else {
            session.setAttribute(key, value);
        }
    }

    /**
     * 设置登陆属性
     *
     * @param request 请求信息
     * @param loginKey 登录键
     * @param userLoginRedisInfo 用户信息
     */
    public void setLongAttribute(HttpServletRequest request, String loginKey, UserLoginRedisInfo userLoginRedisInfo) {
        this.setAttribute(request, loginKey, userLoginRedisInfo, true);
    }

    /**
     * 删除属性
     *
     * @param request 请求信息
     * @param key 键
     */
    public void removeAttribute(HttpServletRequest request, String key) {
        HttpSession session = request.getSession();
        session.removeAttribute(key);
    }

    /**
     * 退出登录
     *
     * @param request 请求信息
     */
    public void logout(HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        this.removeAttribute(request, USER_LOGIN_STATE);
        // 删除 IP 和 session 对应的 key
        stringRedisTemplate.delete(RedisKeyUtil.getUserExtraInfoKey(loginUser.getId()));
    }

    /**
     * 是否在其他设备登陆
     *
     * @param request 请求信息
     * @param currentIp 当前的 IP
     * @return 如果已经在其它设备登陆，返回其他设备的 sessionId，否则返回空
     */
    public String checkOtherLogin(HttpServletRequest request, Long userId,String currentIp) {
        // 得到 Redis 中存储的信息
        String userExtraInfoKey = RedisKeyUtil.getUserExtraInfoKey(userId);
        Object oldSessionIdObj = stringRedisTemplate.opsForHash().get(userExtraInfoKey, SESSION_ID);
        String oldSessionId = null;
        if (oldSessionIdObj != null) {
            oldSessionId = (String) oldSessionIdObj;
        }
        Object oldIpObj = stringRedisTemplate.opsForHash().get(userExtraInfoKey, IP);
        String oldIp = null;
        if (oldIpObj != null) {
            oldIp = (String) oldIpObj;
        }

        // 根据 sessionId 和 IP 判断
        String currentSessionId = request.getSession().getId();
        if (StringUtils.isNotBlank(oldSessionId) && oldSessionId.equals(currentSessionId)) {
            return null;
        }
        if (StringUtils.isNotBlank(oldIp) && oldIp.equals(currentIp)) {
            return null;
        }
        return oldSessionId;
    }

    /**
     * 删除其它 session 的登陆属性
     *
     * @param sessionId session标识
     * @param userId 用户标识
     */
    public void removeOtherSessionLoginAttribute(String sessionId, Long userId) {
        String sessionKey = RedisKeyUtil.getSessionKey(sessionId);
        String sessionAttrKey = RedisKeyUtil.getSessionAttrKey(USER_LOGIN_STATE);
        // 删除用户的额外信息
        Boolean userExtraInfoDelete = stringRedisTemplate.delete(RedisKeyUtil.getUserExtraInfoKey(userId));
        // 防止数据删不掉
        Long delete = stringRedisTemplate.opsForHash().delete(sessionKey, sessionAttrKey);

        log.info("之前的sessionId: {}, 用户的额外信息删除结果: {}, 用户登录态删除解结果: {}", sessionId, userExtraInfoDelete, delete);
    }

    /**
     * 用户登录
     *
     * @param request 请求信息
     * @param user 用户
     * @return 提示信息
     */
    public String login(HttpServletRequest request, User user) {
        String message = "登陆成功";
        String ipAddress = IpUtil.getRequestClientRealIp(request);
        String oldSessionId = this.checkOtherLogin(request, user.getId(), ipAddress);
        if (StringUtils.isNotBlank(oldSessionId)) {
            // 删除 oldSessionId 的登录态
            this.removeOtherSessionLoginAttribute(oldSessionId, user.getId());
            message += ", 已移除其它设备的登录";
        }
        UserLoginRedisInfo userLoginRedisInfo = UserLoginRedisInfo.builder()
                .user(user)
                .ip(ipAddress)
                .build();
        this.setLongAttribute(request, USER_LOGIN_STATE, userLoginRedisInfo);
        return message;
    }
}
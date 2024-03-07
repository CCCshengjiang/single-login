package com.wen.singlelogin.utils;

import static com.wen.singlelogin.constant.RedisKeyConstant.*;
import static org.springframework.session.data.redis.RedisIndexedSessionRepository.DEFAULT_NAMESPACE;


/**
 * 快速生成 Redis 的 key 的工具类
 *
 * @author wen
 */
public class RedisKeyUtil {

    /**
     * 获取已登陆用户的 IP 和 sessionId 对应的 key
     *
     * @param userId 用户的 id
     * @return {@link String}
     */
    public static String getUserExtraInfoKey(Long userId) {
        return USER_EXTRA_INFO + userId;
    }

    /**
     * 获取 session 信息对应的 key
     *
     * @param sessionId session标识
     * @return {@link String}
     */
    public static String getSessionKey(String sessionId) {
        return DEFAULT_NAMESPACE + ":" + SESSION_KEY_POSTFIX + ":" + sessionId;
    }

    /**
     * 获取 session 中某一属性的key
     *
     * @param attrName 属性名称
     * @return {@link String}
     */
    public static String getSessionAttrKey(String attrName) {
        return SESSION_ATTRIBUTE_PREFIX + ":" + attrName;
    }

}

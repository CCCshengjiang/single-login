package com.wen.singlelogin.constant;

/**
 * Redis 生成 key 要用到的多个常量
 *
 * @author wen
 */
public interface RedisKeyConstant {

    // IP 和 sessionId 的 key 前缀
    String USER_EXTRA_INFO = "user:extra:";

    // Spring Session 中的 session 信息的后缀（sessionId 前面）
    String SESSION_KEY_POSTFIX = "sessions";

    // session 中保存的属性的前缀
    String SESSION_ATTRIBUTE_PREFIX = "sessionAttr";

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    String IP = "ip";

    String SESSION_ID = "sessionId";

}

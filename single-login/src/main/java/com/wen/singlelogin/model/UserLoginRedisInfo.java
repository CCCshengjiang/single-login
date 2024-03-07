package com.wen.singlelogin.model;

import lombok.Builder;
import lombok.Data;

/**
 * 用户登录的 Redis 的 value 信息
 * 
 * @author wen
 */
@Data
@Builder
public class UserLoginRedisInfo {
    
    private User user;

    private String ip;
}
package com.wen.singlelogin.controller;

import com.wen.singlelogin.manage.SessionManage;
import com.wen.singlelogin.model.User;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wen.singlelogin.constant.RedisKeyConstant.USER_LOGIN_STATE;

/**
 * 用户的接口控制层
 *
 * @author wen
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private SessionManage sessionManage;

    @GetMapping("/login")
    public User userLogin(HttpServletRequest request) {
        User user = new User();
        user.setId(1L);
        String login = sessionManage.login(request, user);
        log.info("{}，id：{}", login, user.getId());
        return user;
    }


    @GetMapping("/test")
    public User testLogin(HttpServletRequest request) {
        User user = new User();
        user.setId(1L);
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return user;
    }
}

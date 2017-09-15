package com.jay.controller;

/**
 * Created by Jay on 2017/6/21.
 */
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jay.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.jay.model.User;

import java.io.IOException;


@Controller
@RequestMapping("/user")
// /user/**
public class UserController {
    Logger logger = Logger.getLogger(UserController.class);

    @Resource
    private UserService userService;
    @RequestMapping("/get")
    public void selectUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        int userId = Integer.parseInt(request.getParameter("id"));
        User user = this.userService.selectUser(userId);
        logger.info("user:"+user);
//        ObjectMapper mapper = new ObjectMapper();
//        response.getWriter().write(mapper.writeValueAsString(user));
//        response.getWriter().close();
    }
}

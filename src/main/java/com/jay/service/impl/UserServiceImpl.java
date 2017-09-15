package com.jay.service.impl;

import com.jay.dao.UserDao;
import com.jay.model.User;
import com.jay.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Jay on 2017/6/21.
 */
@Service("userService")
public class UserServiceImpl  implements UserService {

    @Resource
    private UserDao userDao;

    public User selectUser(int userId) {
        return this.userDao.selectUser(userId);
    }

}

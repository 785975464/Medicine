package com.jay.dao;

import com.jay.model.User;

/**
 * Created by Jay on 2017/6/21.
 */
public interface UserDao {
    User selectUser(int id);
}

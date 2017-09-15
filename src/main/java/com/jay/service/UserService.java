package com.jay.service;

import com.jay.model.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Jay on 2017/6/21.
 */
@Transactional
public interface UserService {
    User selectUser(int userId);
}

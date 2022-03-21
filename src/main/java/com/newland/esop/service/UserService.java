package com.newland.esop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.newland.esop.pojo.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends IService<User> {
    User login(String username,String password);
}

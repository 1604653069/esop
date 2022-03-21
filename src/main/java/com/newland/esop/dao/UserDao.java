package com.newland.esop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newland.esop.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<User> {
}

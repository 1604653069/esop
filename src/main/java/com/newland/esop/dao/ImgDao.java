package com.newland.esop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newland.esop.pojo.Img;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ImgDao extends BaseMapper<Img> {

    List<Img> getByIds(@Param("fIds") List<Long> id);
}

package com.newland.esop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.newland.esop.pojo.Img;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ImgService extends IService<Img> {
    List<Img> getByIds(List<Long> id);
    //删除所有的图片
    void removeByIds(@Param("ids")List<Long> id);
}

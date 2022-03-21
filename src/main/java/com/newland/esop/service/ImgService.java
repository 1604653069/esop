package com.newland.esop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.newland.esop.pojo.Img;
import java.util.List;


public interface ImgService extends IService<Img> {
    List<Img> getByIds(List<Long> id);
}

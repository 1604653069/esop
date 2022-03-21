package com.newland.esop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newland.esop.dao.ImgDao;
import com.newland.esop.pojo.Img;
import com.newland.esop.service.ImgService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("imgService")
public class ImgServiceImpl extends ServiceImpl<ImgDao, Img> implements ImgService {
    @Override
    public List<Img> getByIds(List<Long> ids) {
        return baseMapper.getByIds(ids);
    }
}

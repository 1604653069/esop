package com.newland.esop.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newland.esop.dao.AppUpdateDao;
import com.newland.esop.pojo.AppUpdate;
import com.newland.esop.service.AppUpdateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUpdateServiceImpl extends ServiceImpl<AppUpdateDao, AppUpdate> implements AppUpdateService {

    @Override
    public AppUpdate getUpdate(int code,int type) {
        QueryWrapper<AppUpdate> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("code",code).eq("type",type);
        AppUpdate appUpdate = baseMapper.selectOne(queryWrapper);

        return appUpdate;
    }

    @Override
    public List<AppUpdate> getUpdateList() {
        return baseMapper.selectList(null);
    }

    @Override
    public boolean deleteFile(int id) {
        int i = baseMapper.deleteById(id);
        if (i>0)
            return true;
        else
            return false;
    }
}

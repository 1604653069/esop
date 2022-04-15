package com.newland.esop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.newland.esop.pojo.AppUpdate;
import com.newland.esop.pojo.ChildrenFolder;

import java.util.List;

public interface AppUpdateService extends IService<AppUpdate> {
    AppUpdate getUpdate(int code,int type);

    List<AppUpdate> getUpdateList();

    boolean deleteFile(int id);
}

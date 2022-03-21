package com.newland.esop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newland.esop.pojo.UploadFolder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UploadFolderDao extends BaseMapper<UploadFolder> {
    String getFolderDirPath(Long id);
}

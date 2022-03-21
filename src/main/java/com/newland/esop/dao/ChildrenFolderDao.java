package com.newland.esop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newland.esop.pojo.ChildrenFolder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChildrenFolderDao extends BaseMapper<ChildrenFolder> {
    List<ChildrenFolder> getChildrenFolderByIds(@Param("fIds") List<Long> fIds);
}

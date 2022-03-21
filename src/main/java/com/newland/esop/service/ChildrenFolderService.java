package com.newland.esop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.newland.esop.pojo.ChildrenFolder;
import com.newland.esop.pojo.UploadFolder;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

public interface ChildrenFolderService extends IService<ChildrenFolder> {

    List<ChildrenFolder> getChildrenFolderByIds(List<Long> fIds);

    boolean addChildrenFolder(Long fid,String name, List<MultipartFile> multipartFile);

    boolean updateChildrenFolder(Long fid,Long id,String name,List<MultipartFile> multipartFiles);

    boolean deleteFolderImg(Long fid,Long id);

    UploadFolder getFolderByChildId(Long id);

}

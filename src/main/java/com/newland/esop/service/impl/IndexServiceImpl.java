package com.newland.esop.service.impl;

import com.newland.esop.pojo.ChildrenFolder;
import com.newland.esop.pojo.Img;
import com.newland.esop.pojo.IndexData;
import com.newland.esop.pojo.UploadFolder;
import com.newland.esop.service.ChildrenFolderService;
import com.newland.esop.service.ImgService;
import com.newland.esop.service.IndexService;
import com.newland.esop.service.UploadFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexServiceImpl implements IndexService {
    @Autowired
    private ImgService imgService;
    @Autowired
    private ChildrenFolderService childrenFolderService;
    @Autowired
    private UploadFolderService uploadFolderService;
    @Override
    public IndexData getIndexData() {
        IndexData indexData = new IndexData();
        List<Img> list = imgService.list();
        List<UploadFolder> uploadFolders = uploadFolderService.list();
        List<ChildrenFolder> childrenFolderList = childrenFolderService.list();
        if (list!=null||list.size()>0) {
            indexData.setImgNum(list.size());
        }
        if (uploadFolders!=null||list.size()>0) {
            indexData.setFlowNum(uploadFolders.size());
        }
        if (childrenFolderList!=null||childrenFolderList.size()>0) {
            indexData.setDeviceNum(childrenFolderList.size());
        }
        return indexData;
    }
}

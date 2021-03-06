package com.newland.esop.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.newland.esop.pojo.ImageUpload;
import com.newland.esop.pojo.UploadFolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UploadFolderService extends IService<UploadFolder> {
    /**
     * 上传文件（zip）
     * @param uploadFiles
     * @return
     */
    boolean uploadFile(List<MultipartFile> uploadFiles);

    /**
     * 获取所有的流水线对应上传的文件夹
     * @return
     */
    List<UploadFolder> findAllFolder();

    /**
     * 通过id获取流水文件夹
     * @param id
     * @return
     */
    UploadFolder findFolderById(Long id);

    /**
     * 通过id获取流水线的文件夹名称
     * @param id
     * @return
     */
    String getFolderDirPath(Long id);

    /**
     * 删除整个流水线
     * @param id
     * @return
     */
    boolean deleteFolder(Long id);

    ImageUpload uploadImage(MultipartFile multipartFile);

    /**
     * 替换某条流水线下的所有的信息
     * @param multipartFile
     * @return
     */
    boolean replaceAll(Long id,MultipartFile multipartFile);

    boolean updateAPP(int type,int code,MultipartFile multipartFile);
}

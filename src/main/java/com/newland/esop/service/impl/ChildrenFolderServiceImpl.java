package com.newland.esop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newland.esop.common.FileConstants;
import com.newland.esop.dao.ChildrenFolderDao;
import com.newland.esop.dao.UploadFolderDao;
import com.newland.esop.pojo.ChildrenFolder;
import com.newland.esop.pojo.Img;
import com.newland.esop.pojo.UploadFolder;
import com.newland.esop.service.ChildrenFolderService;
import com.newland.esop.service.ImgService;
import com.newland.esop.utils.FTPUtils;
import com.newland.esop.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Service("childrenFolderService")
public class ChildrenFolderServiceImpl extends ServiceImpl<ChildrenFolderDao, ChildrenFolder> implements ChildrenFolderService {
    @Autowired
    private FTPUtils ftpUtils;

    @Autowired
    private UploadFolderDao uploadFolderDao;

    @Autowired
    private ImgService imgService;
    @Override
    public List<ChildrenFolder> getChildrenFolderByIds(List<Long> fIds) {
        return baseMapper.getChildrenFolderByIds(fIds);
    }

    /**
     * 创建工位操作手册说明书
     */
    @Override
    public boolean addChildrenFolder(Long fid, String name, List<MultipartFile> multipartFiles) {
        try {
        //1.获取流水线的文件夹名称
        String folderDirPath = uploadFolderDao.getFolderDirPath(fid);
        //2.创建文件夹
        File file = new File(ftpUtils.getCURRENT_DIR()+"/"+folderDirPath+"/"+name);
        if (!file.exists()) {
            file.mkdir();
        } else {
            //文件名重复
            System.out.println("文件名重复");
            return false;
        }
        boolean isSave = false;
        Long id =null;
        for (MultipartFile multipartFile:multipartFiles) {
            //3.获取源文件的名称
            String originName = multipartFile.getOriginalFilename().substring(0,multipartFile.getOriginalFilename().lastIndexOf("."));
            //4.创建新文件名称
            String uuid = UUIDUtils.getUUID();
            //5.获取源文件的后缀
            String suffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            //6.将图片上传到改文件夹中
            boolean b = ftpUtils.uploadToFtpDir(multipartFile.getInputStream(), folderDirPath+"/"+name, uuid+suffix, false);
            if (b) {
                //6.1查看数据库中是否有存在的改子目录
                ChildrenFolder childrenFolder = new ChildrenFolder();
                childrenFolder.setFid(fid);
                childrenFolder.setFileName(name);
                //6.1上传成功后，保存一份到数据库中
                if (!isSave) {
                    //如果存在上传多个文件，目录只保存一次在数据库中
                    baseMapper.insert(childrenFolder);
                    isSave = true;
                    id = childrenFolder.getId();
                }
                //7.保存图片信息到数据库中
                Img img = new Img();
                img.setOriginName(originName);
                img.setFid(id);
                img.setFileName(uuid+suffix);
                img.setImgUrl(FileConstants.SHOW_DIR+folderDirPath+"/"+childrenFolder.getFileName()+"/"+img.getFileName());
                imgService.save(img);
            } else {
                //上传失败
                return false;
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean updateChildrenFolder(Long fid,Long id, String name, List<MultipartFile> multipartFiles) {
        //1.获取流水线的文件夹名称
        String folderDirPath = uploadFolderDao.getFolderDirPath(fid);
        //2.获取之前的文件夹名称
        ChildrenFolder childrenFolder = baseMapper.selectById(id);
        String originName = childrenFolder.getFileName();
        File file = new File(ftpUtils.getCURRENT_DIR()+"/"+folderDirPath+"/"+originName);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files!=null) {
                for (File file1:files) {
                    if (file1.isFile())
                        file1.delete();
                }
            }
            file.delete();
        }
        //从数据库中删除子文件夹和图片
        //删除图片
        QueryWrapper<Img> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fid",id);
        imgService.remove(queryWrapper);
        //删除文件夹
        baseMapper.deleteById(id);
        //删除后添加，即修改文件
        addChildrenFolder(fid,name,multipartFiles);
        return true;
    }

    @Override
    public boolean deleteFolderImg(Long fid,Long id) {
        ChildrenFolder childrenFolder = baseMapper.selectById(fid);
        UploadFolder uploadFolder = uploadFolderDao.selectById(childrenFolder.getFid());
        File file = new File(ftpUtils.getCURRENT_DIR()+"/"+uploadFolder.getFileName()+"/"+childrenFolder.getFileName());
        //2.查看改目录下是否还有其他图片信息，没有则删除改子目录
        QueryWrapper<Img> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fid",fid);
        List<Img> list = imgService.list(queryWrapper);
        if (list!=null && list.size()>0) {
            for (Img img:list) {
                if (img.getId() == id) {
                    //删除目录中的文件
                    File[] files = file.listFiles();
                    if (files!=null) {
                        for (File file1:files) {
                            if (file1.getName().equals(img.getFileName()))
                                file1.delete();
                        }
                    }
                    //删除数据库中的数据
                    imgService.removeById(img.getId());
                }
            }
        }
        File[] files = file.listFiles();
        if (files.length==0) {
            file.delete();
            baseMapper.deleteById(fid);
        }
        return true;
    }

    @Override
    public UploadFolder getFolderByChildId(Long id) {
        ChildrenFolder childrenFolder = baseMapper.selectById(id);
        QueryWrapper<Img> imgQueryWrapper = new QueryWrapper<>();
        imgQueryWrapper.eq("fid",childrenFolder.getId());
        List<Img> list = imgService.list(imgQueryWrapper);
        if (list!=null&&list.size()>0) {
            childrenFolder.setImgList(list);
        }
        UploadFolder uploadFolder = uploadFolderDao.selectById(childrenFolder.getFid());
        List<ChildrenFolder> childrenFolderList = new ArrayList<>();
        childrenFolderList.add(childrenFolder);
        uploadFolder.setChildrenFolderList(childrenFolderList);
        return uploadFolder;
    }


}

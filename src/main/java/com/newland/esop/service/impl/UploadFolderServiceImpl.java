package com.newland.esop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newland.esop.common.FileConstants;
import com.newland.esop.dao.UploadFolderDao;
import com.newland.esop.pojo.ChildrenFolder;
import com.newland.esop.pojo.Img;
import com.newland.esop.pojo.UploadFolder;
import com.newland.esop.service.ChildrenFolderService;
import com.newland.esop.service.ImgService;
import com.newland.esop.service.UploadFolderService;
import com.newland.esop.utils.FTPUtils;
import com.newland.esop.utils.UUIDUtils;
import net.lingala.zip4j.core.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("uploadFolderService")
public class UploadFolderServiceImpl extends ServiceImpl<UploadFolderDao, UploadFolder> implements UploadFolderService {

    //1.ftp工具
    @Autowired
    private FTPUtils ftpUtils;

    @Autowired
    private ChildrenFolderService childrenFolderService;

    @Autowired
    private ImgService imgService;
    /**
     * 将上传的文件保存在数据库中
     * @param uploadFiles
     * @return
     */
    @Override
    public boolean uploadFile(List<MultipartFile> uploadFiles) {
        String originFileName = "";
        //1.遍历上传的文件
        for (MultipartFile uploadFile:uploadFiles) {
            try {
                //保存上传的原件名
                originFileName = uploadFile.getOriginalFilename().substring(0,uploadFile.getOriginalFilename().lastIndexOf("."));
                System.out.println("原上传的名称:"+originFileName);
                //重命名原上传文件
                String uuid = UUIDUtils.getUUID();
                String fileName = uuid+uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf("."));
                System.out.println("生成的文件名称:"+uuid);
                //2.上传文件
                boolean b = ftpUtils.uploadToFtp(uploadFile.getInputStream(), uploadFile.getOriginalFilename(), false);
                if (!b) {
                    //文件上传失败
                    return false;
                }
                //上传成功后保存到数据库一份
                UploadFolder folder = new UploadFolder();
                folder.setFileName(uuid);
                folder.setOriginName(originFileName);
                baseMapper.insert(folder);
                //3.解压文件,
                ZipFile zipFile = new ZipFile(ftpUtils.getCURRENT_DIR()+ File.separator+uploadFile.getOriginalFilename());
                zipFile.setFileNameCharset("gbk");
                //解压的目录
                zipFile.extractAll(ftpUtils.getCURRENT_DIR());
                //修改解压后的名称
                File fileDir = new File(ftpUtils.getCURRENT_DIR());
                    File[] files = fileDir.listFiles();
                for (File file :files) {
                    if (file.getName().equals(originFileName) && file.isDirectory()) {
                        System.out.println("遍历的文件名称:"+file.getName());
                        File[] files1 = file.listFiles();
                        for (File file1:files1) {
                            ChildrenFolder childrenFolder = new ChildrenFolder();
                            childrenFolder.setFid(folder.getId());
                            childrenFolder.setFileName(file1.getName());
                            childrenFolderService.save(childrenFolder);
                            File[] files2 = file1.listFiles();
                            for (File file2:files2) {
                                System.out.println(file2.getName());
                                Img img = new Img();
                                img.setFid(childrenFolder.getId());
                                img.setOriginName(file2.getName());
                                String s = UUIDUtils.getUUID() + file2.getName().substring(file2.getName().lastIndexOf("."));
                                img.setFileName(s);
                                img.setImgUrl(FileConstants.SHOW_DIR+folder.getFileName()+"/"+childrenFolder.getFileName()+"/"+img.getFileName());
                                imgService.save(img);
                                file2.renameTo(new File(ftpUtils.getCURRENT_DIR()+File.separator+file.getName()+"/"+childrenFolder.getFileName()+"/"+s));
                            }
                        }
                        //重命名
                        file.renameTo(new File(ftpUtils.getCURRENT_DIR()+File.separator+uuid));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.toString());
                return false;
            }
        }
        return true;
    }

    /**
     * 获取所有的流水线的文件
     * @return
     */
    @Override
    public List<UploadFolder> findAllFolder() {
        //获取所有的流水文件
        List<UploadFolder> folders = baseMapper.selectList(null);
        //1.收集所有的父文件ids
        List<Long> folderIds = folders.stream().map(folder -> folder.getId()).collect(Collectors.toList());
        //2.获取所有的子文件
        List<ChildrenFolder> childrenFolders = childrenFolderService.getChildrenFolderByIds(folderIds);
        //3.收集所有的子文件的id
        List<Long> childrenFolderIds = childrenFolders.stream().map(childrenFolder -> childrenFolder.getId()).collect(Collectors.toList());
        //4.获取所有的图片
        List<Img> imgList = imgService.getByIds(childrenFolderIds);
        System.out.println("查询到的图片为"+imgList.toString());
        //5.进行包装
        //5.1将图片包装到上级文件中
        collectImgToChildrenFolder(childrenFolders, imgList);
        //5.2将上级文件封装到顶级文件中
        for (UploadFolder uploadFolder:folders) {
            List<ChildrenFolder> childrenFolderList = new ArrayList<>();
            for (ChildrenFolder childrenFolder:childrenFolders) {
                if (childrenFolder.getFid() == uploadFolder.getId()) {
                    childrenFolderList.add(childrenFolder);
                }
            }
            uploadFolder.setChildrenFolderList(childrenFolderList);
        }
        return folders;
    }

    @Override
    public UploadFolder findFolderById(Long id) {
        //1.获取流水线的文件夹
        UploadFolder folder = baseMapper.selectById(id);
        //2.获取流水线下的子文件夹
        QueryWrapper<ChildrenFolder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fid",folder.getId());
        List<ChildrenFolder> childrenFolders = childrenFolderService.list(queryWrapper);
        //3.收集所有的子文件id
        List<Long> childrenIds = childrenFolders.stream().map(file -> file.getId()).collect(Collectors.toList());
        //4.根据子文件id获取所有的图片
        List<Img> imgs = imgService.getByIds(childrenIds);
        System.out.println("获取所有的图片为："+imgs);
        //5.将图片设置到上级目录中
        collectImgToChildrenFolder(childrenFolders, imgs);
        folder.setChildrenFolderList(childrenFolders);
        return folder;
    }

    /**
     * 通过id获取文件夹的名称
     * @param id
     * @return
     */
    @Override
    public String getFolderDirPath(Long id) {
        return baseMapper.selectById(id).getFileName();
    }

    /**
     * 将图片封装到上级文件中
     * @param childrenFolders
     * @param imgs
     */
    private void collectImgToChildrenFolder(List<ChildrenFolder> childrenFolders, List<Img> imgs) {
        for (ChildrenFolder childrenFolder:childrenFolders) {
            List<Img> imgList = new ArrayList<>();
            for (Img img:imgs) {
                if (img.getFid() == childrenFolder.getId()) {
                    imgList.add(img);
                }
            }
            childrenFolder.setImgList(imgList);
        }
    }

}

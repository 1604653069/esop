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
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


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
    public boolean addChildrenFolder2(Long fid, String name,List<String> filenames) {
        ChildrenFolder childrenFolder = new ChildrenFolder();
        //1.获取流水线的文件夹名称
        String folderDirPath = uploadFolderDao.getFolderDirPath(fid);
        //2.创建文件夹
        File file = new File(ftpUtils.getCURRENT_DIR()+"/"+folderDirPath+"/"+name);
        if (!file.exists()) {
            file.mkdir();
            childrenFolder.setFileName(name);
            childrenFolder.setFid(fid);
            baseMapper.insert(childrenFolder);
        } else {
            //文件名重复
            System.out.println("文件名重复");
            return false;
        }
        //复制文件到指定的文件夹
        for (String filename:filenames) {
            File file1 = new File(ftpUtils.getCURRENT_DIR() + "/" + filename);
            Img img = new Img();
            moveFileToDir(childrenFolder, folderDirPath, name, filename, img, file, file1);
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
    public boolean updateChildrenFolder2(Long fid, String ids, String filenames) {
        System.out.println("fid--->"+fid);
        System.out.println("ids--->"+ids);
        System.out.println("filenames--->"+filenames);
        //1.判断是否删除原先的图片
        System.out.println(StringUtils.hasLength(ids)+""+ids.contains(","));
        if (StringUtils.hasLength(ids)) {
            //之前的图片有删除
            if (ids.contains(",")) {
                //多个回传图片被删除
                List<Integer> imgIds = Arrays.stream(ids.split(",")).map(id->Integer.parseInt(id)).collect(Collectors.toList());
                for (int id:imgIds) {
                    delImgById(id);
                }
            } else {
                //单个文件被删除
                System.out.println(Integer.parseInt(ids));
                delImgById(Integer.parseInt(ids));
            }
        }

        ChildrenFolder childrenFolder = baseMapper.selectById(fid);
        UploadFolder uploadFolder = uploadFolderDao.selectById(childrenFolder.getFid());
        String folderDirPath = uploadFolder.getFileName();
        String name = childrenFolder.getFileName();

        //将临时文件移动到指定的文件夹中
        if (StringUtils.hasLength(filenames)) {
            if (filenames.contains(",")) {
                //多个文件上传
                List<String> uploadFileNames = Arrays.stream(filenames.split(",")).collect(Collectors.toList());
                //复制文件到指定的文件夹
                for (String filename:uploadFileNames) {
                    //临时文件
                    Img img = new Img();
                    File file = new File(ftpUtils.getCURRENT_DIR()+"/"+uploadFolder.getFileName()+"/"+childrenFolder.getFileName());
                    File file1 = new File(ftpUtils.getCURRENT_DIR() + "/" + filename);
                    moveFileToDir(childrenFolder, folderDirPath, name, filename, img, file, file1);
                }
            } else {
                Img img = new Img();
                File file = new File(ftpUtils.getCURRENT_DIR()+"/"+uploadFolder.getFileName()+"/"+childrenFolder.getFileName());
                File file1 = new File(ftpUtils.getCURRENT_DIR() + "/" + filenames);
                moveFileToDir(childrenFolder, folderDirPath, name, filenames, img, file, file1);
            }
        }
        return false;
    }

    private void moveFileToDir(ChildrenFolder childrenFolder, String folderDirPath, String name, String filename, Img img, File file, File file1) {
        try {
            FileUtils.moveFileToDirectory(file1,file,false);
            String suffix = filename.substring(filename.lastIndexOf("-")+1);
            File file2 = new File(ftpUtils.getCURRENT_DIR() + "/" +folderDirPath+"/"+name+"/"+filename);
            if (file2.exists()) {
                file2.renameTo(new File(ftpUtils.getCURRENT_DIR() + "/" +folderDirPath+"/"+name+"/"+suffix));
                img.setFid(childrenFolder.getId());
                img.setOriginName(filename.substring(0,filename.lastIndexOf("-")));
                img.setFileName(suffix);
                img.setImgUrl(FileConstants.SHOW_DIR+folderDirPath+"/"+name+"/"+suffix);
                imgService.save(img);
            }
        } catch (IOException e) {
            e.printStackTrace();
            imgService.removeById(img);
        }
    }

    @Override
    public boolean deleteFolderImg(Long fid,Long id) {
        ChildrenFolder childrenFolder = baseMapper.selectById(fid);
        UploadFolder uploadFolder = uploadFolderDao.selectById(childrenFolder.getFid());
        File file = new File(ftpUtils.getCURRENT_DIR()+"/"+uploadFolder.getFileName()+"/"+childrenFolder.getFileName());
        //2.查看改目录下是否还有其他图片信息，没有则删除改子目录
        QueryWrapper<Img> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fid",fid);
        //删除数据中的数据
        imgService.remove(queryWrapper);
        baseMapper.deleteById(fid);
        //删除目录
        com.newland.esop.utils.FileUtils.deleteDir(new File(ftpUtils.getCURRENT_DIR()+"/"+uploadFolder.getFileName()+"/"+childrenFolder.getFileName()));
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

    @Override
    public ChildrenFolder getFolderByName(String name) {
        QueryWrapper<ChildrenFolder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_name",name);
        ChildrenFolder childrenFolder = baseMapper.selectOne(queryWrapper);
        QueryWrapper<Img> imgQueryWrapper = new QueryWrapper<>();
        imgQueryWrapper.eq("fid", childrenFolder.getId());
        List<Img> list = imgService.list(imgQueryWrapper);
        childrenFolder.setImgList(list);
        return childrenFolder;
    }

    private void delImgById(int id) {
        Img img = imgService.getById(id);
        ChildrenFolder childrenFolder = baseMapper.selectById(img.getFid());
        UploadFolder uploadFolder = uploadFolderDao.selectById(childrenFolder.getFid());
        File file = new File(ftpUtils.getCURRENT_DIR()+"/"+uploadFolder.getFileName()+"/"+childrenFolder.getFileName()+"/"+img.getFileName());
        if (file.exists() && file.isFile()) {
            file.delete();
            imgService.removeById(id);
        }
    }

}

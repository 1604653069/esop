package com.newland.esop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.newland.esop.common.FileConstants;
import com.newland.esop.dao.UploadFolderDao;
import com.newland.esop.pojo.*;
import com.newland.esop.service.AppUpdateService;
import com.newland.esop.service.ChildrenFolderService;
import com.newland.esop.service.ImgService;
import com.newland.esop.service.UploadFolderService;
import com.newland.esop.utils.FTPUtils;
import com.newland.esop.utils.FileUtils;
import com.newland.esop.utils.UUIDUtils;
import net.lingala.zip4j.core.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private AppUpdateService appUpdateService;
    /**
     * 将上传的文件保存在数据库中
     * @param uploadFiles
     * @return
     */
    @Override
    public boolean uploadFile(List<MultipartFile> uploadFiles) {
        String originFileName = "";
        String suffix = "";
        //1.遍历上传的文件
        for (MultipartFile uploadFile:uploadFiles) {
            try {
                //保存上传的原件名
                originFileName = uploadFile.getOriginalFilename().substring(0,uploadFile.getOriginalFilename().lastIndexOf("."));
                suffix = uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf("."));
                System.out.println("原上传的名称:"+originFileName);
                System.out.println("原上传的后缀:"+suffix);
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
                //删除上传的zip文件
                File uploadZipFile = new File(ftpUtils.getCURRENT_DIR()+"/"+originFileName+suffix);
                if (uploadZipFile.exists()) {
                    uploadZipFile.delete();
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
                if (img.getFid().intValue() == childrenFolder.getId().intValue()) {
                    imgList.add(img);
                }
            }
            childrenFolder.setImgList(imgList);
        }
    }

    /**
     * 删除整条流水线
     */
    @Override
    public boolean deleteFolder(Long id) {
        //删除ftp上的文件
        //获取要删除目录的文件名
        UploadFolder uploadFolder = baseMapper.selectById(id);
        String fileName = uploadFolder.getFileName();
        System.out.println("删除的文件名为:"+fileName);
        File file = new File(ftpUtils.getCURRENT_DIR()+"/"+fileName);
        if (file.exists()) {
            //如果存在文件，则删除
            //存在该文件
            System.out.println("存在该文件");
            FileUtils.deleteDir(file);
            //删除数据库中的请求信息
            QueryWrapper<ChildrenFolder> childrenFolderQueryWrapper = new QueryWrapper<>();
            childrenFolderQueryWrapper.eq("fid",id);
            List<ChildrenFolder> childrenFolders = childrenFolderService.list(childrenFolderQueryWrapper);
            List<Long> childrenIds = childrenFolders.stream().map(childrenFolder -> childrenFolder.getId()).collect(Collectors.toList());
            //删除所有的图片
            imgService.removeByIds(childrenIds);
            //删除所有的子目录
            childrenFolderService.removeByIds(childrenIds);
            //删除流水目录
            baseMapper.deleteById(id);
        } else {
            System.out.println("删除的文件不存在!");
            return false;
        }
        return true;
    }

    /**
     * 上传到临时文件
     * @return
     */
    @Override
    public ImageUpload uploadImage(MultipartFile multipartFile) {
        ImageUpload imageUpload = new ImageUpload();
        try {
        //1.获取源文件的名称
        String originName = multipartFile.getOriginalFilename().substring(0,multipartFile.getOriginalFilename().lastIndexOf("."));
        System.out.println("源文件名称:"+originName);
        //2.创建新文件名称
        String uuid = UUIDUtils.getUUID();
        System.out.println("重命名文件名称:"+uuid);
        //5.获取源文件的后缀
        String suffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        imageUpload.setFilename(originName+"-"+uuid+suffix);
        boolean b = ftpUtils.uploadToFtp(multipartFile.getInputStream(), originName+"-"+uuid+suffix, false);
        if (b) {
            System.out.println("文件上传成功");
            imageUpload.setResult(true);
        } else {
            System.out.println("文件上传失败");
            imageUpload.setResult(false);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUpload;
    }

    /**
     * 替换某条流水下的所有文件
     */
    @Override
    public boolean replaceAll(Long id,MultipartFile uploadFile) {
        try {
            String originFileName = uploadFile.getOriginalFilename().substring(0,uploadFile.getOriginalFilename().lastIndexOf("."));
            System.out.println("上传的文件为:"+originFileName);
            String suffix = uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf("."));
            System.out.println("上传文件的后缀为:"+suffix);
            //1.将要替换的文件上传到ftp中
            ftpUtils.uploadToFtp(uploadFile.getInputStream(),uploadFile.getOriginalFilename(),false);
            //2.解压文件,
            ZipFile zipFile = new ZipFile(ftpUtils.getCURRENT_DIR()+ File.separator+uploadFile.getOriginalFilename());
            zipFile.setFileNameCharset("gbk");
            //3.解压的目录
            zipFile.extractAll(ftpUtils.getCURRENT_DIR());
            //4.从数据库中获取文件夹名称，并删除
            UploadFolder uploadFolder = baseMapper.selectById(id);

            //删除数据库中的数据
            QueryWrapper<ChildrenFolder> queryWrapper = new QueryWrapper();
            queryWrapper.eq("fid",id);

            List<ChildrenFolder> childrenFolders = childrenFolderService.list(queryWrapper);
            System.out.println("子文件夹为:"+childrenFolders.toString());
            List<Long> childrenFoldersId = childrenFolders.stream().map(childrenFolder -> childrenFolder.getId()).collect(Collectors.toList());
            //删除图片
            imgService.removeByIds(childrenFoldersId);
            //删除文件夹
            childrenFolderService.remove(queryWrapper);

            File fileDir = new File(ftpUtils.getCURRENT_DIR());
            File[] files = fileDir.listFiles();
            for (File file :files) {
                if (file.getName().equals(originFileName) && file.isDirectory()) {
                    System.out.println("遍历的文件名称:"+file.getName());
                    File[] files1 = file.listFiles();
                    for (File file1:files1) {
                        ChildrenFolder childrenFolder = new ChildrenFolder();
                        childrenFolder.setFid(id);
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
                            img.setImgUrl(FileConstants.SHOW_DIR+uploadFolder.getFileName()+"/"+childrenFolder.getFileName()+"/"+img.getFileName());
                            imgService.save(img);
                            file2.renameTo(new File(ftpUtils.getCURRENT_DIR()+File.separator+file.getName()+"/"+childrenFolder.getFileName()+"/"+s));
                        }
                    }
                    //删除原先的文件夹
                    FileUtils.deleteDir(new File(ftpUtils.getCURRENT_DIR()+File.separator+"/"+uploadFolder.getFileName()));
                    //重命名
                    file.renameTo(new File(ftpUtils.getCURRENT_DIR()+File.separator+uploadFolder.getFileName()));
                }
            }
            //删除上传的zip文件
            File uploadZipFile = new File(ftpUtils.getCURRENT_DIR()+"/"+originFileName+suffix);
            if (uploadZipFile.exists()) {
                uploadZipFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateAPP(int type, int code, MultipartFile multipartFile) {

        //1.上传文件的原名称
        String originName = multipartFile.getOriginalFilename().substring(0,multipartFile.getOriginalFilename().lastIndexOf("."));
        System.out.println("上传文件的原名称:"+originName);
        //2.上传文件的后缀
        String suffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        System.out.println("上传文件的后缀:"+suffix);
        String dir ="";
        //3.获取上传文件的类型
        if (type == 1) {
            //上传类型为APP
            dir = "apk";
        } else if(type == 2) {
            //上传类型为机顶盒
            dir = "box";
        }
        //创建文件夹
        File file = new File(ftpUtils.getCURRENT_DIR()+"/"+dir);
        //文件不存在则创建文件
        if (!file.exists()) {
            file.mkdir();
        }
        //将文件上传到指定的目录上
        String uuid = UUIDUtils.getUUID();
        try {
            ftpUtils.uploadToFtpDir(multipartFile.getInputStream(), file.getName(),uuid+suffix,false);
            System.out.println("文件上传成功");
            //删除数据库中所有之前的上级文件
            QueryWrapper<AppUpdate> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("type",type);
            appUpdateService.remove(queryWrapper);

            AppUpdate appUpdate = new AppUpdate();
            appUpdate.setCode(code);
            appUpdate.setType(type);
            appUpdate.setFileName(uuid+suffix);
            appUpdate.setUrl(FileConstants.SHOW_DIR+file.getName()+"/"+(uuid+suffix));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String uploadTime = simpleDateFormat.format(new Date());
            appUpdate.setUploadDate(uploadTime);
            appUpdateService.save(appUpdate);
        } catch (Exception e) {
            System.out.println("文件上传失败");
            return false;
        }

        return true;
    }
}

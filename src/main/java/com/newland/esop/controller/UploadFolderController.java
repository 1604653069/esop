package com.newland.esop.controller;

import com.newland.esop.common.R;
import com.newland.esop.pojo.ImageUpload;
import com.newland.esop.pojo.UploadFolder;
import com.newland.esop.service.TempService;
import com.newland.esop.service.UploadFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("upload")
public class UploadFolderController {

    @Autowired
    private UploadFolderService uploadFolderService;

    @Autowired
    private TempService tempService;
    @PostMapping("/file")
    public R uploadFolder(@RequestParam("uploadFiles") List<MultipartFile> uploadFiles) {
        boolean b = uploadFolderService.uploadFile(uploadFiles);
        if (b)
            return R.ok();
        else
            return R.error();
    }

    @GetMapping("/all")
    public R getAllFolder() {
        List<UploadFolder> allFolder = uploadFolderService.findAllFolder();
        return R.ok().put("data",allFolder);
    }

    @GetMapping("/getFolder")
    public R getFolderById(@RequestParam("id")Long id) {
        UploadFolder folder = uploadFolderService.findFolderById(id);
        return R.ok().put("data",folder);
    }

    @GetMapping("/deleteFolder")
    public R deleteFolder(@RequestParam("id")Long id) {
        boolean b = uploadFolderService.deleteFolder(id);
        if (b) {
            return R.ok();
        } else {
            return R.error();
        }

    }

    @PostMapping("/image")
    public R uploadAddImg(@RequestParam("uploadFile") MultipartFile multipartFile) {
        ImageUpload imageUpload = uploadFolderService.uploadImage(multipartFile);
        if (imageUpload.isResult()) {
            return R.ok().put("data",imageUpload);
        } else {
            return R.error();
        }
    }

    @PostMapping("/replaceAll")
    private R replaceAll(@RequestParam("id")Long id,@RequestParam("fileUpload") MultipartFile multipartFile) {
        System.out.println("上传的id为:"+id);
        System.out.println("上传的文件名称为:"+multipartFile.getOriginalFilename());
        uploadFolderService.replaceAll(id,multipartFile);
        return R.ok();
    }

    @GetMapping("/delImg")
    public R uploadDelImg(@RequestParam("filename")String filename) {
        tempService.delImg(filename);
        return R.ok();
    }

    @PostMapping("/updateFile")
    public R uploadUpdateFile(@RequestParam("type")int type,@RequestParam("code")int code,@RequestParam("fileupload")MultipartFile multipartFile) {
        System.out.println("上传的文件类型 type--->"+type);
        System.out.println("上传的文件 code --->"+code);
        System.out.println("上传的文件名称 name--->"+multipartFile.getOriginalFilename());
        uploadFolderService.updateAPP(type,code,multipartFile);
        return R.ok();
    }
}

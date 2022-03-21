package com.newland.esop.controller;

import com.newland.esop.common.R;
import com.newland.esop.pojo.UploadFolder;
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
}

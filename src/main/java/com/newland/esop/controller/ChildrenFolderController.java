package com.newland.esop.controller;

import com.newland.esop.common.R;
import com.newland.esop.pojo.UploadFolder;
import com.newland.esop.service.ChildrenFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("children")
public class ChildrenFolderController {
    @Autowired
    private ChildrenFolderService childrenFolderService;

    /**
     * 添加流水上的工位操作说明书
     * @return
     */
    @PostMapping("/addChildrenFolder")
    public R addChildrenFolder(@RequestParam("fid")Long fid, @RequestParam("name")String name, @RequestParam("uploadFile") List<MultipartFile> multipartFile) {
        boolean b = childrenFolderService.addChildrenFolder(fid, name, multipartFile);
        if (b) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @PostMapping("/updateChildrenFolder")
    public R updateChildrenFolder(@RequestParam("fid")Long fid,@RequestParam("id")Long id,@RequestParam("name")String name, @RequestParam("uploadFile") List<MultipartFile> multipartFile) {
        boolean b = childrenFolderService.updateChildrenFolder(fid, id, name, multipartFile);
        if (b) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @GetMapping("/deleteImg")
    public R deleteChildrenFolderOrImg(@RequestParam("fid")Long fid,@RequestParam("id")Long id) {
        boolean b = childrenFolderService.deleteFolderImg(fid, id);
        if (b) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @GetMapping("/getChildrenImg")
    public R getFolderByChildId(@RequestParam("id")Long id) {
        UploadFolder folder = childrenFolderService.getFolderByChildId(id);
        return R.ok().put("data",folder);
    }
}

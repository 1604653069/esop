package com.newland.esop.controller;

import com.newland.esop.common.R;
import com.newland.esop.pojo.AppUpdate;
import com.newland.esop.service.AppUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app")
public class AppController {

    @Autowired
    private AppUpdateService appUpdateService;

    @RequestMapping("/update")
    public R appUpdate(@RequestParam("code")int code,@RequestParam("type")int type) {
        AppUpdate update = appUpdateService.getUpdate(code,type);
        if (update!=null) {
            return R.ok().put("data",update);
        } else {
            return R.error(0,"暂无升级文件");
        }
    }

    @GetMapping("/list")
    public R getUpdateList() {
        List<AppUpdate> updateList = appUpdateService.getUpdateList();
        return R.ok().put("data",updateList);
    }

    @GetMapping("/delete")
    public R deleteFile(@RequestParam("id")int id) {
        appUpdateService.deleteFile(id);
        return R.ok();
    }
}

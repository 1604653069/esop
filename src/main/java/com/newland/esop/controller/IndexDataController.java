package com.newland.esop.controller;

import com.newland.esop.common.R;
import com.newland.esop.pojo.IndexData;
import com.newland.esop.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class IndexDataController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/data")
    private R getIndexData() {
        IndexData indexData = indexService.getIndexData();
        return R.ok().put("data",indexData);
    }
}

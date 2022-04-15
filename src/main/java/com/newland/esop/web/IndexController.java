package com.newland.esop.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {
    @GetMapping("index")
    public String index() {
        return "index";
    }

    @GetMapping("/image")
    public String image() {
        return "image";
    }

    @GetMapping("/addImage")
    public String addImage() {
        return "addImage";
    }

    @GetMapping("/updateImage")
    public String editImage() {
        return "updateImage";
    }

    @GetMapping("/flow")
    public String flow() {
        return "flow";
    }

    @GetMapping("/addFlow")
    public String addFlow() {
        return "addFlow";
    }

    @GetMapping("/replaceAll")
    public String replaceAll() {
        return "replaceAll";
    }

    @GetMapping("/update")
    public String update() {
        return "update";
    }

    @GetMapping("/updateFile")
    public String fileList() {
        return "updateFile";
    }
}

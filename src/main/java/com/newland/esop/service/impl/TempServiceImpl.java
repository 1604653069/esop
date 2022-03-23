package com.newland.esop.service.impl;

import com.newland.esop.service.TempService;
import com.newland.esop.utils.FTPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class TempServiceImpl implements TempService {
    @Autowired
    private FTPUtils ftpUtils;

    @Override
    public void delImg(String filename) {
        File file = new File(ftpUtils.getCURRENT_DIR()+"/"+filename);
        if (file.exists()) {
            System.out.println("文件存在");
            file.delete();
        } else {
            System.out.println("文件不存在");
        }
    }
}

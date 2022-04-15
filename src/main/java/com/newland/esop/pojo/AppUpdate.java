package com.newland.esop.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

@Data
@TableName("tbl_update")
@ToString
public class AppUpdate {
    private Long id;
    private int code;
    private int type;
    private String fileName;
    private String url;
    private String uploadDate;
}

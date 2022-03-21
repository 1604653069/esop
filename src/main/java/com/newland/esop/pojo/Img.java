package com.newland.esop.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@TableName("tbl_img")
public class Img {
    private Long id;
    private String originName;
    private String fileName;
    private String imgUrl;
    private Long fid;
}

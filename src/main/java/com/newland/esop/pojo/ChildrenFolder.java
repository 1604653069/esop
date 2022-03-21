package com.newland.esop.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@TableName("tbl_children_folder")
public class ChildrenFolder {
    private Long id;
    //源文件名称
    private String fileName;
    //父级文件id
    private Long fid;
    //文件下的图片
    @TableField(exist = false)
    private List<Img> imgList;
}

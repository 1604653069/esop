package com.newland.esop.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@TableName("tbl_folder")
public class UploadFolder {
    private Long id;
    //源文件名称
    private String originName;
    //展示的文件名称
    private String fileName;
    //子文件
    @TableField(exist = false)
    private List<ChildrenFolder> childrenFolderList;
}

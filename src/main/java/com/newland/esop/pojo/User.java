package com.newland.esop.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@TableName("tbl_user")
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
}

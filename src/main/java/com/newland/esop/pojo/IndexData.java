package com.newland.esop.pojo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class IndexData {
    private int deviceNum;
    private int userNum =1;
    private int imgNum;
    private int flowNum;
}

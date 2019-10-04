package com.henu.henuxin.VO;

import lombok.Data;

/**
 * @Author: F
 * @Date: 2019/9/29 10:36
 */
@Data
public class ResultVO<T> {
    private Integer code;
    private String msg;
    private T data;
}

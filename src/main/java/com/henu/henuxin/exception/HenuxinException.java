package com.henu.henuxin.exception;

import com.henu.henuxin.enums.HenuxinErrCode;
import lombok.Getter;

/**
 * @Author: F
 * @Date: 2019/9/29 10:54
 */
@Getter
public class HenuxinException extends RuntimeException{
    private Integer code;
    private String msg;

    public HenuxinException(HenuxinErrCode errCode) {
        this.code =errCode.getCode();
        this.msg = errCode.getMsg();
    }
}

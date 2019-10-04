package com.henu.henuxin.enums;

import lombok.Getter;

/**
 * @Author: F
 * @Date: 2019/9/29 10:40
 */
@Getter
public enum HenuxinErrCode {
    ERROR_MSG(500,"服务器错误"),
    ERROR_MAP(501,"error"),
    ERROR_TOKEN_MSG(502,"errorTokenMsg"),
    ERROR_EXCEPTION(555,"msg"),
    FILE_UPLOAD_FAIL(556,"文件上传失败")

    ;
    private Integer code;
    private String msg;

    HenuxinErrCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }}

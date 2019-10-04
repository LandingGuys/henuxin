package com.henu.henuxin.utils;

import org.springframework.util.Base64Utils;

/**
 * @Author: F
 * @Date: 2019/10/3 20:45
 */
public class ByteUtil {
    public static byte[] base64ToByte(String base64Data)  throws Exception {
        String dataPrix = "";
        String data = "";

        if(base64Data == null || "".equals(base64Data)){
            return null;
        }else{
            String [] d = base64Data.split("base64,");
            if(d != null && d.length == 2){
                dataPrix = d[0];
                data = d[1];
            }else{
                return null;
            }
        }

        // 因为BASE64Decoder的jar问题，此处使用spring框架提供的工具包
        byte[] bs = Base64Utils.decodeFromString(data);
        return bs;
    }
}

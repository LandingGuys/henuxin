package com.henu.henuxin.utils;

import com.henu.henuxin.VO.ResultVO;
import com.henu.henuxin.exception.HenuxinException;

/**
 * @Author: F
 * @Date: 2019/9/29 10:44
 */
public class ResultVOUtil {
    public static ResultVO success(Object object){
        ResultVO resultVO=new ResultVO();
        resultVO.setData(object);
        resultVO.setCode(200);
        resultVO.setMsg("OK");
        return resultVO;
    }
    public static ResultVO success(){
        return success(null);
    }
    public static ResultVO error(Integer code,String msg){
        ResultVO resultVO=new ResultVO();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        return  resultVO;
    }
    public static ResultVO error(HenuxinException e){
        ResultVO resultVO=new ResultVO();
        resultVO.setCode(e.getCode());
        resultVO.setMsg(e.getMsg());
        return  resultVO;
    }

}

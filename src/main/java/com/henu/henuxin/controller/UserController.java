package com.henu.henuxin.controller;

import com.henu.henuxin.dto.UserDTO;
import com.henu.henuxin.model.User;
import com.henu.henuxin.service.UserService;
import com.henu.henuxin.utils.MD5Util;
import com.henu.henuxin.utils.ResultVOUtil;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: F
 * @Date: 2019/9/29 10:35
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注册
     * @return
     */
    @PostMapping("/register")
    public Object register(@RequestBody User user){
        if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            return ResultVOUtil.error(1001,"用户名或密码不能为空");
        }
       if(userService.repeatName(user.getUsername())){
           user.setNickname(user.getUsername());
           user.setFaceImage("");
           user.setFaceImageBig("");
           user.setPassword(MD5Util.md5(user.getPassword()));
           user.setId(Sid.nextShort());
           user.setQrcode("");
           boolean insert = userService.insert(user);
           if(insert){
               return ResultVOUtil.success("注册成功");
           }
       }else{
           return ResultVOUtil.error(1002,"用户名已注册过");
       }

        return null;
    }
    @PostMapping("/repeatName")
    public Object repeatName(String username){
        if(userService.repeatName(username)){
            return ResultVOUtil.success();
        }else{
            return ResultVOUtil.error(1002,"用户名已注册过");
        }

    }
    @PostMapping("/login")
    public Object login(@RequestBody User user){

        if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            return ResultVOUtil.error(1001,"用户名或密码不能为空");
        }
        User searchUser = userService.searchUser(user.getUsername(), MD5Util.md5(user.getPassword()));
        if(searchUser!=null){
            return ResultVOUtil.success(searchUser);
        }
        return ResultVOUtil.error(1003,"登录失败");

    }


    @PostMapping("/setNickname")
    public Object setNickname(@RequestBody UserDTO userDTO){
        int i = userService.updateUser(userDTO);
        if(i>0){
            User user = userService.searchUserById(userDTO.getUserId());
            return ResultVOUtil.success(user);
        }else{
            return ResultVOUtil.error(1005,"修改失败");
        }

    }

}

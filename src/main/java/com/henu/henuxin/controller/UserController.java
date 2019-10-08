package com.henu.henuxin.controller;

import com.henu.henuxin.VO.MyFriendsVO;
import com.henu.henuxin.VO.UserVO;
import com.henu.henuxin.dto.UserDTO;
import com.henu.henuxin.enums.OperatorFriendRequestTypeEnum;
import com.henu.henuxin.enums.SearchFriendsStatusEnum;
import com.henu.henuxin.model.ChatMsg;
import com.henu.henuxin.model.User;
import com.henu.henuxin.service.UserService;
import com.henu.henuxin.utils.MD5Util;
import com.henu.henuxin.utils.ResultVOUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
           boolean insert = userService.insert(user);
           if(insert){
               return ResultVOUtil.success("注册成功");
           }
       }else{
           return ResultVOUtil.error(1002,"用户名已注册过");
       }

        return null;
    }

    /**
     * 校验用户名是否重复
     * @param username
     * @return
     */
    @PostMapping("/repeatName")
    public Object repeatName(String username){
        if(userService.repeatName(username)){
            return ResultVOUtil.success();
        }else{
            return ResultVOUtil.error(1002,"用户名已注册过");
        }

    }

    /**
     * 登录
     * @param user
     * @return
     */
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

    /**
     * 修改昵称
     * @param userDTO
     * @return
     */
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

    /**
     * 搜索好友接口，根据账户做匹配查询而不是模糊查询
     * @param myUserId
     * @param friendUsername
     * @return
     */
    @PostMapping("/search")
    public Object search(@RequestParam("myUserId") String myUserId,
                         @RequestParam("friendUsername") String friendUsername) {
        //0.p判断myUserId friendUsername 不能为空
        if(StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)){
            return ResultVOUtil.error(1006,"");
        }
        //1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if(status== SearchFriendsStatusEnum.SUCCESS.status){
            User user = userService.searchByUsername(friendUsername);
            UserVO userVO=new UserVO();
            BeanUtils.copyProperties(user,userVO);
            return ResultVOUtil.success(userVO);
        }else{
            String errorMsg=SearchFriendsStatusEnum.getMsgByKey(status);
            return ResultVOUtil.error(1007,errorMsg);
        }
    }

    /**
     * 添加好友的请求
     * @param myUserId
     * @param friendUsername
     * @return
     */
    @PostMapping("/addFriendRequest")
    public Object addFriendRequest(@RequestParam("myUserId") String myUserId,
                         @RequestParam("friendUsername") String friendUsername) {

        if(StringUtils.isBlank(myUserId) ||
            StringUtils.isBlank(friendUsername)){
            return ResultVOUtil.error(1006,"");
        }
        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if (status == SearchFriendsStatusEnum.SUCCESS.status) {
            userService.sendFriendRequest(myUserId, friendUsername);
        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return ResultVOUtil.error(1007,errorMsg);
        }
        return ResultVOUtil.success();
    }

    /**
     * 查询好友的请求
     * @param userId
     * @return
     */
    @PostMapping("/queryFriendRequests")
    public Object queryFriendRequests(String userId) {
        // 0. 判断不能为空
        if (StringUtils.isBlank(userId)) {
            return ResultVOUtil.error(1006,"");
        }
        // 1. 查询用户接受到的朋友申请
        return ResultVOUtil.success(userService.queryFriendRequestList(userId));
    }

    /**
     * 接受方 通过或者忽略朋友请求
     * @param acceptUserId
     * @param sendUserId
     * @param operType
     * @return
     */
    @PostMapping("/operFriendRequest")
    public Object operFriendRequest(String acceptUserId, String sendUserId,
                                             Integer operType) {

        // 0. acceptUserId sendUserId operType 判断不能为空
        if (StringUtils.isBlank(acceptUserId)
                || StringUtils.isBlank(sendUserId)
                || operType == null) {
            return ResultVOUtil.error(1006,"");
        }

        // 1. 如果operType 没有对应的枚举值，则直接抛出空错误信息
        if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))) {
            return ResultVOUtil.error(1006,"");
        }

        if (operType == OperatorFriendRequestTypeEnum.IGNORE.type) {
            // 2. 判断如果忽略好友请求，则直接删除好友请求的数据库表记录
            userService.deleteFriendRequest(sendUserId, acceptUserId);
        } else if (operType == OperatorFriendRequestTypeEnum.PASS.type) {
            // 3. 判断如果是通过好友请求，则互相增加好友记录到数据库对应的表
            //	   然后删除好友请求的数据库表记录
            userService.passFriendRequest(sendUserId, acceptUserId);
        }

        // 4. 数据库查询好友列表
        List<MyFriendsVO> myFirends = userService.queryMyFriends(acceptUserId);

        return ResultVOUtil.success(myFirends);
    }
    @PostMapping("/myFriends")
    public Object myFriends(String userId) {
        // 0. userId 判断不能为空
        if (StringUtils.isBlank(userId)) {
            return ResultVOUtil.error(1006,"");
        }

        // 1. 数据库查询好友列表
        List<MyFriendsVO> myFirends = userService.queryMyFriends(userId);

        return ResultVOUtil.success(myFirends);
    }

    /**
     * 用户手机端获取未签收的消息列表
     * @param acceptUserId
     * @return
     */
    @PostMapping("/getUnReadMsgList")
    public Object getUnReadMsgList(String acceptUserId) {
        // 0. userId 判断不能为空
        if (StringUtils.isBlank(acceptUserId)) {
            return ResultVOUtil.error(1006,"");
        }

        // 查询列表
        List<ChatMsg> unReadMsgList = userService.getUnReadMsgList(acceptUserId);

        return ResultVOUtil.success(unReadMsgList);
    }

}

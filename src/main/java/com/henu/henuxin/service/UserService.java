package com.henu.henuxin.service;

import com.henu.henuxin.VO.FriendRequestVO;
import com.henu.henuxin.VO.MyFriendsVO;
import com.henu.henuxin.dto.UserDTO;
import com.henu.henuxin.model.User;
import com.henu.henuxin.netty.ChatMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: F
 * @Date: 2019/9/29 11:44
 */
public interface UserService {

    /**
     * 判断用户名重复
     */
    public boolean repeatName(String username);

    /**
     * 插入用户
     * @param user
     */
    public boolean insert(User user);

    /**
     * 根据用户名和密码查询用户
     * @param username
     * @param password
     */
    public User searchUser(String username,String password);

    /**
     * 根据用户需求修改用户信息
     * @param userDTO
     * @return
     */
    public int updateUser(UserDTO userDTO);

    /**
     * 通过用户userId查询用户
     * @param userId
     * @return
     */
    public User searchUserById(String userId);

    /**
     * 通过用户Username查询用户
     * @param username
     * @return
     */
    public User searchByUsername(String username);

    /**
     * 搜索用户前置条件，返回查询状态
     * @param myUserId
     * @param friendUsername
     * @return
     */
    Integer preconditionSearchFriends(String myUserId, String friendUsername);

    /**
     * 发送添加好友请求
     * @param myUserId
     * @param friendUsername
     */
    void sendFriendRequest(String myUserId, String friendUsername);

    /**
     * 根据接收者id查询请求好友列表
     * @param userId
     * @return
     */
    List<FriendRequestVO> queryFriendRequestList(String userId);

    /**
     * 根据发送者id，接受者id删除好友请求
     * @param sendUserId
     * @param acceptUserId
     */
    void deleteFriendRequest(String sendUserId, String acceptUserId);

    /**
     * 根据发送者id，接受者通过好友请求，并添加在好友列表
     * @param sendUserId
     * @param acceptUserId
     */
    void passFriendRequest(String sendUserId, String acceptUserId);

    /**
     * 根据用户id，查询好友列表
     * @param acceptUserId
     * @return
     */
    List<MyFriendsVO> queryMyFriends(String acceptUserId);

    String saveMsg(ChatMsg chatMsg);

    void updateMsgSigned(ArrayList<String> msgIdList);

    List<com.henu.henuxin.model.ChatMsg> getUnReadMsgList(String acceptUserId);
}

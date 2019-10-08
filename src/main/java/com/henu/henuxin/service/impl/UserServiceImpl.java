package com.henu.henuxin.service.impl;

import com.alibaba.fastjson.JSON;
import com.henu.henuxin.VO.FriendRequestVO;
import com.henu.henuxin.VO.MyFriendsVO;
import com.henu.henuxin.dto.UserDTO;
import com.henu.henuxin.enums.MsgActionEnum;
import com.henu.henuxin.enums.MsgSignFlagEnum;
import com.henu.henuxin.enums.SearchFriendsStatusEnum;
import com.henu.henuxin.mapper.*;
import com.henu.henuxin.model.*;
import com.henu.henuxin.netty.ChatMsg;
import com.henu.henuxin.netty.DataContent;
import com.henu.henuxin.netty.UserChannelRel;
import com.henu.henuxin.provider.AliYunProvider;
import com.henu.henuxin.service.UserService;
import com.henu.henuxin.utils.FileUtils;
import com.henu.henuxin.utils.MD5Util;
import com.henu.henuxin.utils.QRCodeUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: F
 * @Date: 2019/9/29 11:44
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MyFriendsMapper myFriendsMapper;
    @Autowired
    private UsersMapperCustom usersMapperCustom;
    @Autowired
    private FriendsRequestMapper friendsRequestMapper;
    @Autowired
    private ChatMsgMapper chatMsgMapper;
    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private AliYunProvider aliYunProvider;
    
    @Override
    public boolean repeatName(String username) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(username);
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size()!=0){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean insert(User user) {
        String userId=Sid.nextShort();
        user.setNickname(user.getUsername());
        user.setFaceImage("http://shuixin.oss-cn-beijing.aliyuncs.com/20191006/1570359198680191004GF3HB2WPX4.png?Expires=1885719198&OSSAccessKeyId=LTAIlAHWpVWuoRQH&Signature=JPVnsLdscRCaKgi9Jj6UpxWUldk%3D");
        user.setFaceImageBig("");
        user.setPassword(MD5Util.md5(user.getPassword()));
        user.setId(userId);

        //为每一个用户生成一个唯一的二维码
        //henuxin_qrcode:[username]
        String qrCodePath = "F://user" + userId + "qrcode.png";
        qrCodeUtils.createQRCode(qrCodePath, "henuxin_qrcode:" + user.getUsername());
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);
        String fileName=userId+"qrcode.png";
        String qrCodeUrl="";
        try {
            qrCodeUrl=aliYunProvider.upload(qrCodeFile.getInputStream(),fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        user.setQrcode(qrCodeUrl);

        int res = userMapper.insertSelective(user);
        if(res!=0){
            log.info("插入成功");
            return true;
        }else{
            log.info("插入失败");
            return false;
        }

    }

    @Override
    public User searchUser(String username, String password) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(username)
                .andPasswordEqualTo(password);
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size()!=0){

            log.info("查询成功");
            return users.get(0);
        }else{
            log.info("查询失败");
            return null;
        }
    }

    @Override
    public int updateUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getUserId());
        user.setNickname(userDTO.getNickname());
        int i = userMapper.updateByPrimaryKeySelective(user);
        return i;
    }

    @Override
    public User searchUserById(String userId) {
        User user= userMapper.selectByPrimaryKey(userId);
        return user;
    }

    @Override
    public Integer preconditionSearchFriends(String myUserId, String friendUsername) {
        User user = searchByUsername(friendUsername);
        //1.搜索的用户如果不存在，返回【无此用户】
        if(user==null){
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }
        //2.搜索账号是你自己，返回【不能添加自己】
        if(user.getId().equals(myUserId)){
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }
        //3.搜索的朋友已经是你的好友，返回【该用户已经是你的好友】
        MyFriendsExample myFriendsExample = new MyFriendsExample();
        myFriendsExample.createCriteria().andMyUserIdEqualTo(myUserId)
                .andMyFriendsUserIdEqualTo(user.getId());
        List<MyFriends> myFriends = myFriendsMapper.selectByExample(myFriendsExample);
        if (myFriends.size()!=0){
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }
        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    @Override
    public User searchByUsername(String username) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(username);
        List<User> users = userMapper.selectByExample(userExample);
        return users.get(0);
    }

    @Transactional
    @Override
    public void sendFriendRequest(String myUserId, String friendUsername) {
        // 根据用户名把朋友信息查询出来
        User friend = searchByUsername(friendUsername);

        // 1. 查询发送好友请求记录表
        FriendsRequestExample friendsRequestExample = new FriendsRequestExample();
        friendsRequestExample.createCriteria().andSendUserIdEqualTo(myUserId)
                .andAcceptUserIdEqualTo(friend.getId());
        List<FriendsRequest> friendsRequests = friendsRequestMapper.selectByExample(friendsRequestExample);
        if (friendsRequests.size() == 0) {
            // 2. 如果不是你的好友，并且好友记录没有添加，则新增好友请求记录
            String requestId = Sid.nextShort();
            FriendsRequest request = new FriendsRequest();
            request.setId(requestId);
            request.setSendUserId(myUserId);
            request.setAcceptUserId(friend.getId());
            request.setRequestDateTime(new Date());
            friendsRequestMapper.insert(request);
        }
    }
    @Override
    public List<FriendRequestVO> queryFriendRequestList(String userId) {
        List<FriendRequestVO> friendRequestVOS = usersMapperCustom.queryFriendRequestList(userId);
        return friendRequestVOS;
    }
    @Transactional
    @Override
    public void deleteFriendRequest(String sendUserId, String acceptUserId) {
        FriendsRequestExample friendsRequestExample = new FriendsRequestExample();
        friendsRequestExample.createCriteria().andSendUserIdEqualTo(sendUserId)
                .andAcceptUserIdEqualTo(acceptUserId);
        friendsRequestMapper.deleteByExample(friendsRequestExample);
    }
    @Transactional
    @Override
    public void passFriendRequest(String sendUserId, String acceptUserId) {
        saveFriends(sendUserId, acceptUserId);
        saveFriends(acceptUserId, sendUserId);
        deleteFriendRequest(sendUserId,acceptUserId);

        Channel sendChannel = UserChannelRel.get(sendUserId);
        if(sendChannel!=null){
            // 使用websocket主动推送消息到请求发起者，更新他的通讯录列表为最新
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);

            sendChannel.writeAndFlush(new TextWebSocketFrame(
                    JSON.toJSONString(dataContent)
            ));


        }

    }

    private void saveFriends(String sendUserId, String acceptUserId) {
        MyFriends myFriends = new MyFriends();
        String recordId = Sid.nextShort();
        myFriends.setId(recordId);
        myFriends.setMyFriendsUserId(acceptUserId);
        myFriends.setMyUserId(sendUserId);
        myFriendsMapper.insert(myFriends);
    }
    @Override
    public List<MyFriendsVO> queryMyFriends(String acceptUserId) {
        List<MyFriendsVO> myFriendsVOS = usersMapperCustom.queryMyFriends(acceptUserId);
        return myFriendsVOS;
    }
    @Transactional
    @Override
    public String saveMsg(ChatMsg chatMsg) {
        com.henu.henuxin.model.ChatMsg msgDB=new com.henu.henuxin.model.ChatMsg();
        String msgId = Sid.nextShort();
        msgDB.setId(msgId);
        msgDB.setAcceptUserId(chatMsg.getReceiverId());
        msgDB.setSendUserId(chatMsg.getSenderId());
        msgDB.setCreateTime(new Date());
        msgDB.setSignFlag(MsgSignFlagEnum.unsign.type);
        msgDB.setMsg(chatMsg.getMsg());
        chatMsgMapper.insert(msgDB);
        return msgId;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateMsgSigned(ArrayList<String> msgIdList) {
        usersMapperCustom.batchUpdateMsgSigned(msgIdList);
    }

    @Override
    public List<com.henu.henuxin.model.ChatMsg> getUnReadMsgList(String acceptUserId) {

        ChatMsgExample chatMsgExample = new ChatMsgExample();
        chatMsgExample.createCriteria().andSignFlagEqualTo(0)
                .andAcceptUserIdEqualTo(acceptUserId);
        List<com.henu.henuxin.model.ChatMsg> chatMsgs = chatMsgMapper.selectByExample(chatMsgExample);
        return chatMsgs;
    }
}

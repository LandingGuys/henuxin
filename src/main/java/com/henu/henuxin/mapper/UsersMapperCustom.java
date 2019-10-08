package com.henu.henuxin.mapper;

import com.henu.henuxin.VO.FriendRequestVO;
import com.henu.henuxin.VO.MyFriendsVO;

import java.util.List;

public interface UsersMapperCustom {
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    public List<MyFriendsVO> queryMyFriends(String userId);

    public void batchUpdateMsgSigned(List<String> msgIdList);
}
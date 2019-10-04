package com.henu.henuxin.service.impl;

import com.henu.henuxin.dto.UserDTO;
import com.henu.henuxin.mapper.UserMapper;
import com.henu.henuxin.model.User;
import com.henu.henuxin.model.UserExample;
import com.henu.henuxin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

package com.henu.henuxin.service;

import com.henu.henuxin.dto.UserDTO;
import com.henu.henuxin.model.User;

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


    public User searchUserById(String userId);

}

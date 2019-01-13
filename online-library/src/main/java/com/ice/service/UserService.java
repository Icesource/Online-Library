package com.ice.service;

import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.User;
import com.ice.model.VO.UserVO;
import org.springframework.data.domain.Page;

public interface UserService {

    Page<User> list();

    User findById(String id);


    User register(String username, String password) throws TException;

    TMsg delete(String id) throws TException;

    TMsg<UserVO> update(String id, String password, String[] roleIds) throws TException;

    TMsg<UserVO> login(String userName, String password) throws TException;
}

package com.ice.controller;

import com.ice.common.Constant;
import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.User;
import com.ice.model.VO.UserVO;
import com.ice.repository.UserRepository;
import com.ice.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/list")
    public TMsg<Page<User>> listAllUsers(){
        Pageable pageable = PageRequest.of(0,10, Sort.Direction.ASC, "id");
        return new TMsg<>("000000","获取成功", userRepository.findAll(pageable));
    }

    @PostMapping("/register")
    public TMsg<User> register(@RequestParam String username,
                                 @RequestParam String password) throws TException {
        return new TMsg<>(Constant.SUCCESS, "注册用户成功", userService.register(username, password)) ;
    }

    @GetMapping("/delete")
    public TMsg delete(@RequestParam String id) throws TException {
        return userService.delete(id);
    }

    @PostMapping("/update")
    public TMsg<UserVO> update(@RequestParam String id,
                               @RequestParam(required = false) String password,
                               @RequestParam(required = false) String[] roleIds) throws TException {
        return userService.update(id, password, roleIds);
    }

//    @PostMapping("/login")
//    public TMsg<UserVO> login(@RequestParam String userName,
//                              @RequestParam String password){
//
//    }
}

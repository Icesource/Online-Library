package com.ice.service.impl;

import com.ice.common.*;
import com.ice.common.SecurityConf.CustomUserDetailService;
import com.ice.model.*;
import com.ice.model.VO.UserVO;
import com.ice.repository.*;
import com.ice.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleFunctionRepository roleFunctionRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailService userDetailService;

    @Override
    public Page<User> list() {
        return null;
    }

    @Override
    public User findById(String id) {
        return null;
    }

    @Override
    public User register(String username, String password) throws TException {
        ParamUtil.isNullParam(username, password);
        ParamUtil.isBlankParam(username, password);

        if(userRepository.findByUsername(username).isPresent()) throw new TException(Constant.USER_ALREADY_EXIST, "用户名重复");

        IDWorker idWorker = new IDWorker();
        User user = new User();
        user.setId(idWorker.nextId());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 5000)
    public TMsg delete(String id) throws TException {
        Optional<User> userOptional = userRepository.findById(id);
        if(!userOptional.isPresent()){
            throw new TException(Constant.NULL_ERROR, "要删除的用户不存在");
        }
        User user = userOptional.get();
        userRoleRepository.deleteAllByUserId(user.getId());
        userRepository.delete(user);
        return new TMsg<>(Constant.SUCCESS, "删除用户成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 5000)
    public TMsg<UserVO> update(String id, String password, String[] roleIds) throws TException {

        Optional<User> userOptional = userRepository.findById(id);
        if(!userOptional.isPresent()){
            throw new TException(Constant.NULL_ERROR, "要修改的用户不存在");
        }
        User user = userOptional.get();
        if(password!=null) {
            ParamUtil.isBlankParam(password);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }
        if(roleIds!=null && roleIds.length>0){
            userRoleRepository.deleteAllByUserId(user.getId());
            for (String roleId: roleIds) {
                if(!roleRepository.findById(roleId).isPresent()) throw new TException(Constant.NULL_ERROR, "要授予的角色不存在");
                UserRole userRole = new UserRole();
                userRole.setRoleId(roleId);
                userRole.setUserId(user.getId());
                userRoleRepository.save(userRole);
            }
        }
        return new TMsg<>(Constant.SUCCESS, "更新用户成功", findByUserName(user.getUsername()));
    }

    @Override
    public TMsg<UserVO> login(String userName, String password) throws TException {
        Optional<User> userOptional = userRepository.findByUsername(userName);
        if(!userOptional.isPresent()){
            throw new TException(Constant.USERNAME_NOT_FOUND, "不存在的用户名");
        }
        userDetailService.loadUserByUsername(userName);
        return null;
    }



    public UserVO findByUserName(String userName) {
        Optional<User> userOptional = userRepository.findByUsername(userName);
        if(!userOptional.isPresent()){
            try {
                throw new TException(Constant.NULL_ERROR, "用户名不存在");
            } catch (TException e) {
                e.printStackTrace();
            }
        }
        User user = userOptional.get();
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 获取用户的所有角色列表
        List<Role> tempRoles = new ArrayList<>();
        List<Function> tempFunction = new ArrayList<>();
        List<UserRole> userRoles = userRoleRepository.findAllByUserId(user.getId());
        for (UserRole userRole: userRoles) {
            Optional<Role> role =roleRepository.findById(userRole.getRoleId());
            // 获取用户应该拥有的所有权限
            List<RoleFunction> temp2 = roleFunctionRepository.findAllByRoleId(userRole.getRoleId());
            for (RoleFunction roleFunction: temp2) {
                Optional<Function> function = functionRepository.findById(roleFunction.getFunctionId());
                tempFunction.add(function.get());
            }
            tempRoles.add(role.get());
        }
        userVO.setRoles(tempRoles);
        userVO.setFunctions(tempFunction);

        return userVO;
    }
}

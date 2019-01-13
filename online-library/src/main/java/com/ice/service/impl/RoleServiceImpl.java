package com.ice.service.impl;

import com.ice.common.Constant;
import com.ice.common.IDWorker;
import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.Function;
import com.ice.model.Role;
import com.ice.model.RoleFunction;
import com.ice.model.VO.RoleVO;
import com.ice.repository.FunctionRepository;
import com.ice.repository.RoleFunctionRepository;
import com.ice.repository.RoleRepository;
import com.ice.repository.UserRoleRepository;
import com.ice.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 提供角色维护的服务
 *
 * @Author: IceSource
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleFunctionRepository roleFunctionRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public TMsg<Page<Role>> list(int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.Direction.ASC, "id");
        return new TMsg<>(Constant.SUCCESS, "获取角色成功", roleRepository.findAll(pageable));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 5000)
    public TMsg<RoleVO> add(String name, String[] functionIds) throws TException {
        IDWorker idWorker = new IDWorker();
        Role role = new Role();
        role.setId(idWorker.nextId());
        role.setName(name);
        roleRepository.save(role);

        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(role, roleVO);
        if (functionIds != null && functionIds.length != 0) {
            List<Function> functions = packFunctions(functionIds);
            for (String id : functionIds) {
                RoleFunction roleFunction = new RoleFunction();
                roleFunction.setRoleId(role.getId());
                roleFunction.setFunctionId(id);
                roleFunctionRepository.save(roleFunction);
            }
            roleVO.setFunctions(functions);
        }
        return new TMsg<>(Constant.SUCCESS, "添加角色成功", roleVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 5000)
    public TMsg delete(String id) throws TException {
        Optional<Role> role = roleRepository.findById(id);
        if (!role.isPresent()) {
            throw new TException(Constant.NULL_ERROR, "要删除的角色不存在");
        }
        roleFunctionRepository.deleteAllByRoleId(role.get().getId());
        userRoleRepository.deleteAllByRoleId(role.get().getId());
        roleRepository.delete(role.get());

        return new TMsg<>(Constant.SUCCESS, "删除角色成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 5000)
    public TMsg<Role> update(String id, String name, String[] functionIds) throws TException {
        Optional<Role> roleOptional = roleRepository.findById(id);
        if(!roleOptional.isPresent()){
            throw new TException(Constant.NULL_ERROR, "要更新的角色不存在");
        }
        Role role = roleOptional.get();
        if(name!=null){
            role.setName(name);
            roleRepository.save(role);
        }
        // 根据传入的functionIds更新权限，不传则全部删除
        roleFunctionRepository.deleteAllByRoleId(role.getId());
        if(functionIds!=null&&functionIds.length!=0){
            List<Function> functions = packFunctions(functionIds);
            for (Function f: functions) {
                RoleFunction roleFunction = new RoleFunction();
                roleFunction.setRoleId(role.getId());
                roleFunction.setFunctionId(f.getId());
                roleFunctionRepository.save(roleFunction);
            }
        }
        return new TMsg<>(Constant.SUCCESS, "更新角色成功", null);
    }

    private List<Function> packFunctions(String[] functionIds) throws TException {
        List<Function> functions = new ArrayList<>();
        for (String id : functionIds) {
            Optional<Function> functionOptional = functionRepository.findById(id);
            if (!functionOptional.isPresent()) {
                throw new TException(Constant.NULL_ERROR, "角色需要的权限功能不存在");
            }
            functions.add(functionOptional.get());
        }
        return functions;
    }
}

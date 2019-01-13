package com.ice.controller;

import com.ice.common.Constant;
import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.Role;
import com.ice.model.VO.RoleVO;
import com.ice.repository.RoleRepository;
import com.ice.service.RoleService;
import com.ice.service.impl.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleServiceImpl roleService;

    @GetMapping("/list")
    public TMsg<Page<Role>> list(@RequestParam int currentPage,
                                 @RequestParam int pageSize){
        return roleService.list(currentPage,pageSize);
    }

    @PostMapping("/add")
    public TMsg<RoleVO> add(@RequestParam String name,
                            @RequestParam(required = false) String[] functionIds) throws TException {

        return roleService.add(name, functionIds);
    }

    @PostMapping("/delete")
    public TMsg delete(@RequestParam String id) throws TException {
        return roleService.delete(id);
    }

    @PostMapping("/update")
    public TMsg<Role> update(@RequestParam String id,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) String[] functionIds) throws TException {
        return roleService.update(id,name,functionIds);
    }

}

package com.ice.service;

import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.Role;
import com.ice.model.VO.RoleVO;
import org.springframework.data.domain.Page;

public interface RoleService {

    TMsg<Page<Role>> list(int currentPage, int pageSize);

    TMsg<RoleVO> add(String name, String[] functionIds) throws TException;

    TMsg delete(String id) throws TException;

    TMsg<Role> update(String id, String name, String[] functionIds) throws TException;
}

package com.ice.repository;

import com.ice.model.RoleFunction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleFunctionRepository extends JpaRepository<RoleFunction, String> {

    List<RoleFunction> findAllByRoleId(String roleId);

    List<RoleFunction> findAllByFunctionId(String functionId);

    RoleFunction save(RoleFunction roleFunction);

    void deleteAllByFunctionId(String functionId);

    void deleteAllByRoleId(String roleId);

}

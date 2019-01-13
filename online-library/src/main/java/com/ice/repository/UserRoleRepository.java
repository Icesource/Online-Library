package com.ice.repository;

import com.ice.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository  extends JpaRepository<UserRole, String> {

    List<UserRole> findAllByRoleId(String roleId);

    List<UserRole> findAllByUserId(String userId);

    void deleteAllByRoleId(String roleId);

    void deleteAllByUserId(String userId);

}

package com.ice.repository;

import com.ice.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Page<Role> findAll(Pageable pageable);

    Optional<Role> findById(String id);

    Role save(Role role);

    void delete(Role role);


}

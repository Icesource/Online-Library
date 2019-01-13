package com.ice.repository;

import com.ice.model.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FunctionRepository extends JpaRepository<Function, String> {

    Optional<Function> findById(String id);

    Page<Function> findAll(Pageable pageable);

    Function save(Function function);

    void delete(Function function);
}

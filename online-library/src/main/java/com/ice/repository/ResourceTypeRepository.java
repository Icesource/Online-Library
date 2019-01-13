package com.ice.repository;

import com.ice.model.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceTypeRepository extends JpaRepository<ResourceType, String> {

    Optional<ResourceType> findById(String id);

    List<ResourceType> findAllBySuperiorid(String superiorid);

    ResourceType save(ResourceType resourceType);

    void deleteById(String id);

}

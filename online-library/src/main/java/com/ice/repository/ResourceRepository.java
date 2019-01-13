package com.ice.repository;

import com.ice.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, String> {

    Resource save(Resource resource);

    Optional<Resource> findById(String id);

    @Query(value = "select * from library_resource where if(?1 !='',typeid=?1,1=1) and if(?2 !='',uploaderid=?2,1=1) and if(?3 !='',name like CONCAT('%',?3,'%'), 1=1)",nativeQuery = true)
    Page<Resource> list(Pageable pageable ,String typeid, String uploaderid,  String search);

    void deleteById(String id);
}

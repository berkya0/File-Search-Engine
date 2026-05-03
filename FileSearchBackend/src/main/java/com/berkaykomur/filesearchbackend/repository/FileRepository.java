package com.berkaykomur.filesearchbackend.repository;

import com.berkaykomur.filesearchbackend.model.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    @Query("SELECT f FROM FileEntity f WHERE " +
            "(:name IS NULL OR f.name LIKE %:name%) AND " +
            "(:extensions IS NULL OR f.extension IN :extensions)")
    Page<FileEntity> searchFiles(@Param("name") String name,
                                 @Param("extensions") Set<String> extensions, Pageable pageable);

    List<FileEntity> findByPathIn(List<String> paths);






}

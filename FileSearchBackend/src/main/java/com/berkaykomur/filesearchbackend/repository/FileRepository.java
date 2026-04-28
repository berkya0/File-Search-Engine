package com.berkaykomur.filesearchbackend.repository;

import com.berkaykomur.filesearchbackend.model.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Page<FileEntity> findByNameContainingIgnoreCase(String fileName, Pageable pageable);




}

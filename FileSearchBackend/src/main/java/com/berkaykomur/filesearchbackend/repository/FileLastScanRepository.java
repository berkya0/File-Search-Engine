package com.berkaykomur.filesearchbackend.repository;

import com.berkaykomur.filesearchbackend.model.FileLastScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileLastScanRepository extends JpaRepository<FileLastScan,Integer> {
    Optional<FileLastScan> findById(Integer id);

    @Query("select f.lastScanTime from FileLastScan f order by f.id desc limit 1")
    long findByLastScanTime();
}

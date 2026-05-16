package com.berkaykomur.filesearchbackend.repository;

import com.berkaykomur.filesearchbackend.model.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO files (name, path, size, last_modified, extension, is_deleted) " +
            "SELECT * FROM UNNEST(:names, :paths, :sizes, :lastModifieds, :extensions, :isDeleteds) " +
            "ON CONFLICT (path) DO UPDATE SET " +
            "name = EXCLUDED.name, " +
            "size = EXCLUDED.size, " +
            "last_modified = EXCLUDED.last_modified, " +
            "is_deleted = false",nativeQuery = true)
    void upsertFilesBatch(@Param("names") String[] names,
                          @Param("paths") String[] paths,
                          @Param("sizes") Long[] sizes,
                          @Param("lastModifieds") Long[] lastModifieds,
                          @Param("extensions") String[] extensions,
                          @Param("isDeleteds") Boolean[] isDeleteds);

    @Query("SELECT f.path FROM FileEntity f WHERE f.path LIKE :zone%")
    Set<String> findPathsByZone (@Param("zone") String zone);

    @Transactional
    @Modifying
    @Query("DELETE FROM FileEntity f WHERE f.path IN :paths")
    void deleteAllByPathIn(@Param("paths") Set<String> paths);


}

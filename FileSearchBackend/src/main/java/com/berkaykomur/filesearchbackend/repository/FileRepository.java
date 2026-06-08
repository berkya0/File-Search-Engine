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
import java.util.Optional;
import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    @Query("SELECT f FROM FileEntity f WHERE " +
            "( :query IS NULL OR (f.name LIKE CONCAT('%', :query, '%') OR f.path LIKE CONCAT('%', :query, '%')) ) " +
            "AND ( :extensions IS NULL OR f.extension IN :extensions ) " +
            "ORDER BY " +
            "CASE WHEN (f.name LIKE CONCAT('%', :query, '%')) THEN 0 ELSE 1 END ASC, " +
            "f.isFavorite DESC, " +
            "f.name ASC")
    Page<FileEntity> searchFiles(@Param("query") String query,
                                 @Param("extensions") Set<String> extensions,
                                 Pageable pageable);

    @Query("SELECT f FROM FileEntity f WHERE (:extensions IS NULL OR f.extension IN :extensions) " +
            "ORDER BY f.isFavorite DESC, f.name ASC")
    Page<FileEntity> findAllFiles(@Param("extensions") Set<String> extensions, Pageable pageable);


    List<FileEntity> findByPathIn(List<String> paths);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO files (name, path, size, last_modified, extension, is_deleted, is_favorite,is_directory,last_open) " +
            "SELECT * FROM UNNEST(:names, :paths, :sizes, :lastModifieds, :extensions, :isDeleteds, :isFavorites, :isDirectories,:last_opens) " +
            "ON CONFLICT (path) DO UPDATE SET " +
            "name = EXCLUDED.name, " +
            "size = EXCLUDED.size, " +
            "last_modified = EXCLUDED.last_modified, " +
            "is_deleted = false, " +
            "is_favorite = files.is_favorite,"+
            "is_directory=files.is_directory,"+
            "last_open=files.last_open",
            nativeQuery = true)
    void upsertFilesBatch(@Param("names") String[] names,
                          @Param("paths") String[] paths,
                          @Param("sizes") Long[] sizes,
                          @Param("lastModifieds") Long[] lastModifieds,
                          @Param("extensions") String[] extensions,
                          @Param("isDeleteds") Boolean[] isDeleteds,
                          @Param("isFavorites") Boolean[] isFavorites,
                          @Param("isDirectories")Boolean[]  isDirectories,
                          @Param("last_opens") Long[] lastOpens);

    @Query("SELECT f.path FROM FileEntity f WHERE f.path LIKE CONCAT(:zone, '%')")
    Set<String> findPathsByZone(@Param("zone") String zone);

    @Transactional
    @Modifying
    @Query("DELETE FROM FileEntity f WHERE f.path IN :paths")
    void deleteAllByPathIn(@Param("paths") Set<String> paths);

    Optional<FileEntity> findByPath(String path);

    Set<FileEntity> findByIsDirectoryTrueAndIsFavoriteTrue();

    @Query("SELECT f FROM FileEntity f WHERE f.lastOpen > 0 ORDER BY f.isFavorite DESC, f.lastOpen DESC LIMIT 10")
    List<FileEntity> findTop10RecentFiles();



}

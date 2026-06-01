package com.berkaykomur.filesearchbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SoftDelete;

@Data
@Entity
@Table(name = "files")
@SoftDelete(columnName = "is_deleted")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000,unique = true)
    private String path;
    private long lastModified;

    @Column(name = "is_favorite", nullable = false)
    private boolean isFavorite = false;

    private String extension;
    private Long size;
    private boolean isDirectory=false;

    private long lastOpen=0;


}

package com.berkaykomur.filesearchbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SoftDelete;

@Data
@Entity
@Table(name = "files")
@SoftDelete(columnName = "isDeleted")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000,unique = true)
    private String path;

    private String extension;
    private long lastModified;
    private Long size;


}

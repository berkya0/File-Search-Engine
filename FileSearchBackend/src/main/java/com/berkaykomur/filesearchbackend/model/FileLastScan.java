package com.berkaykomur.filesearchbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "file_last_scan")
public class FileLastScan {
    @Id
    private Integer id=1;
    private long lastScanTime;

}

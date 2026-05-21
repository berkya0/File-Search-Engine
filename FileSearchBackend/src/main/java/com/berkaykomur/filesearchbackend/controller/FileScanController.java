package com.berkaykomur.filesearchbackend.controller;

import com.berkaykomur.filesearchbackend.repository.FileLastScanRepository;
import com.berkaykomur.filesearchbackend.worker.FileCoordinator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
@Slf4j
public class FileScanController {
    private final FileCoordinator fileCoordinator;
    private final FileLastScanRepository fileLastScanRepository;

    @PostMapping("/start")
    public ResponseEntity<String> startScan(@RequestParam String rootPath){
        log.info("Tarama işlemi bu klasörde başladı: {}", rootPath);
        fileCoordinator.startFullProcess(rootPath,false);
        return ResponseEntity.accepted().body("Tarama işlemi arka planda başlatıldı.");

    }
    @PostMapping("/quick")
    public ResponseEntity<String> quickStartScan(){
        fileCoordinator.quickStart();
        return ResponseEntity.accepted().body("Hızlı başlangıç işlemi arka planda başlatıldı.");
    }
    @GetMapping("/is-onboarded")
    public ResponseEntity<Boolean> isOnboarded() {
        boolean hasScanHistory = fileLastScanRepository.count() > 0;
        return ResponseEntity.ok(hasScanHistory);
    }

}

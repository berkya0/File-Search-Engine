package com.berkaykomur.filesearchbackend.controller;

import com.berkaykomur.filesearchbackend.worker.FileCoordinator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
@Slf4j
public class FileScanController {
    private final FileCoordinator fileCoordinator;

    @GetMapping("/start")
    public String startScan(@RequestParam String rootPath){
        log.info("Tarama işlemi bu klasörde başladı: {}", rootPath);
        fileCoordinator.startFullProcess(rootPath);
        return "Tarama tamamlandı!";

    }

}

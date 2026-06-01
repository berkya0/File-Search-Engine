package com.berkaykomur.filesearchbackend.controller;

import com.berkaykomur.filesearchbackend.dto.FileDto;
import com.berkaykomur.filesearchbackend.service.FileGeneralService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/file/general")
@Slf4j
@RequiredArgsConstructor
public class FileGeneralController {

    private final FileGeneralService fileGeneralService;

    @PatchMapping("/set-lastOpen")
    public ResponseEntity<FileDto> setLastOpen(@RequestParam String path,
                                               @RequestParam long lastOpen) {
        FileDto fileDto = fileGeneralService.setLastOpen(path, lastOpen);
        log.info("Gelen istek başarılı bir şekilde yanıtlandı ve dosyanın son açılma tarihi güncellendi");
        return ResponseEntity.ok(fileDto);
    }
    @GetMapping("/get/files")
    public ResponseEntity<List<FileDto>> getTop10Files() {
        List<FileDto> fileDtos = fileGeneralService.getTop10Files();
        log.info("Gelen istek başarılı şekilde yanıtlandı son açılan dosyalar listeleniyorr");
        return ResponseEntity.ok(fileDtos);
    }

}

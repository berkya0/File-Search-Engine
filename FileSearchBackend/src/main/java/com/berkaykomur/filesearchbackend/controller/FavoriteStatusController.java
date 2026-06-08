package com.berkaykomur.filesearchbackend.controller;

import com.berkaykomur.filesearchbackend.dto.FileDto;
import com.berkaykomur.filesearchbackend.service.UpdateFavoriteStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Slf4j
public class FavoriteStatusController {
    private final UpdateFavoriteStatusService favoriteStatusService;

    @PostMapping("/favorite")
    public ResponseEntity<FileDto> updateFavoriteStatus(@RequestParam String path,
                                                        @RequestParam boolean status){
        FileDto updatedStatus= favoriteStatusService.updateFavoriteStatus(path,status);
        log.info("Favori durumu güncelleme isteği başarıyla yanıtlandı");
        return ResponseEntity.ok(updatedStatus);
    }

    @GetMapping("/favorite/directories")
    public ResponseEntity<Set<FileDto>> favoriteDirectories(){
        Set<FileDto> favFolders=favoriteStatusService.favoriteDirectories();
        //log.info("Favori klasörleri listeleme isteği başarıyla yanıtlandı.");
        return ResponseEntity.ok(favFolders);
    }

}

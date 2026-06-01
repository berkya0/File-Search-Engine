package com.berkaykomur.filesearchbackend.service;

import com.berkaykomur.filesearchbackend.exception.DirectoryAlreadyWatched;
import com.berkaykomur.filesearchbackend.worker.FileCoordinator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectoryScanService {

    private final FileCoordinator fileCoordinator;
    private final HotZoneWatchService watcherService;

    public void directoryScan(String directoryPath,boolean includeSubfolders){

        if(watcherService.isPathAlreadyWatchedByHotZone(directoryPath)){
            throw new DirectoryAlreadyWatched("Klasör zaten gözlem altında: "+directoryPath);
        }
        log.info("Seçilen klasör taranmaya başlayacak");
        if(!includeSubfolders){
            fileCoordinator.scanSingleDirectory(directoryPath);
        }
        else{
            fileCoordinator.startFullProcess(directoryPath,false);
        }

    }

}

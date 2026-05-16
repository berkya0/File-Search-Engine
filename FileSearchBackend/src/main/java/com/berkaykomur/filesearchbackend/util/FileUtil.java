package com.berkaykomur.filesearchbackend.util;

import java.nio.file.Path;
import java.util.Set;

public class FileUtil {
    public static String getExtension(Path file) {
        int dotIndex = file.getFileName().toString().lastIndexOf('.');
        if (dotIndex > 0)
            return file.getFileName().toString().substring(dotIndex+1);
        else return null;
    }
    public static Set<String> HOT_ZONE_NAMES = Set.of(
            "Desktop", "Downloads", "Documents", "Pictures"
    );


}

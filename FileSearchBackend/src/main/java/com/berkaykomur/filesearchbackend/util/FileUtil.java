package com.berkaykomur.filesearchbackend.util;

import java.nio.file.Path;

public class FileUtil {
    public static String getExtension(Path file) {
        int dotIndex = file.getFileName().toString().lastIndexOf('.');
        if (dotIndex > 0)
            return file.getFileName().toString().substring(dotIndex+1);
        else return null;
    }

}

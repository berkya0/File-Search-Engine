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
    private static final String USER_HOME = System.getProperty("user.home");

    public static final Set<Path> HOT_ZONE_NAMES = Set.of(
            Path.of(USER_HOME, "Desktop").toAbsolutePath(),
            Path.of(USER_HOME, "Downloads").toAbsolutePath(),
            Path.of(USER_HOME, "Documents").toAbsolutePath(),
            Path.of(USER_HOME, "Pictures").toAbsolutePath()
    );

}

package com.berkaykomur.filesearchbackend.util;

import java.util.Set;

public class FileUtil {
    public static String getExtension(String fileName) {
        if (fileName == null) return null;
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return null;
    }
    private static final String USER_HOME = System.getProperty("user.home").replace("\\", "/");

    public static final Set<String> HOT_ZONE_NAMES = Set.of(
            (USER_HOME + "/Desktop").replaceAll("//", "/"),
            (USER_HOME + "/Downloads").replaceAll("//", "/"),
            (USER_HOME + "/Documents").replaceAll("//", "/"),
            (USER_HOME + "/Pictures").replaceAll("//", "/")
    );

}

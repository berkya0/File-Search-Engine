package com.berkaykomur.filesearchfrontend.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FileUtil {

    public static String getReadableSize(Long fileSize) {
        if(fileSize<1024)return fileSize+"B";
        int exp=(int) (Math.log(fileSize)/Math.log(1024));
        char pre="KMGTPE".charAt(exp-1);
        return String.format("%.3f %sB", fileSize / Math.pow(1024, exp), pre);
    }

    public static String formatMillis(long millis) {
        return formatMillis(millis, "dd-MM-yyyy HH:mm:ss");
    }
    public static String formatMillis(long millis, String pattern) {
        if (millis <= 0) return null;

        LocalDateTime date = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

}

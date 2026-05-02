package com.berkaykomur.filesearchfrontend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public enum FileCategory {
    DOC(Set.of(
            "pdf", "docx", "doc", "txt", "rtf", "odt",
            "xlsx", "xls", "csv", "ods",
            "pptx", "ppt", "key"
    )),
    IMG(Set.of(
                    "jpg", "jpeg", "png", "gif", "bmp", "svg",
                            "webp", "tiff", "ico", "psd", "ai"
    )),
    CODE(Set.of(
            "java", "py", "cpp", "c", "cs", "js", "ts",
            "html", "css", "scss", "json", "xml", "yaml",
            "sh", "bat", "ps1", "sql", "md", "php"
    )),
    MUSIC(Set.of(
            "mp3", "wav", "flac", "m4a", "aac", "ogg", "wma", "mid"
    )),
    VIDEO( Set.of(
            "mp4", "mkv", "avi", "mov", "wmv", "flv", "webm", "mpeg"
    ));

    private final Set<String> extensions;

    public static Set<String> getExtensionsById(String buttonId) {
        return switch (buttonId) {
            case "btnDoc"   -> DOC.extensions;
            case "btnImg"   -> IMG.extensions;
            case "btnCode"  -> CODE.extensions;
            case "btnMusic"   -> MUSIC.extensions;
            case "btnVideo"  -> VIDEO.extensions;
            default         -> null;
        };
    }


}

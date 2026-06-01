package com.berkaykomur.filesearchfrontend.util;

import com.berkaykomur.filesearchfrontend.dto.FileDto;
import com.berkaykomur.filesearchfrontend.enums.FileCategory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;

public class IconProvider {

    private static final Map<String, Image> iconCache = new HashMap<>();

    public static ImageView getIconForExtension(FileDto file) {
        if (file.isDirectory()) {
            return createImageView("folder.png");
        }

        String extension = file.getExtension();
        if (extension == null || extension.isBlank()) {
            return createImageView("default.png");
        }

        String ext = extension.toLowerCase();
        String iconName;

        if (ext.equals("pdf")) iconName = "pdf.png";
        else if (ext.equals("txt")) iconName = "txt.png";
        else if (FileCategory.DOC.getExtensions().contains(ext)) iconName = "doc.png";
        else if (FileCategory.IMG.getExtensions().contains(ext)) iconName = "img.png";
        else if (FileCategory.CODE.getExtensions().contains(ext)) iconName = "code.png";
        else if (FileCategory.MUSIC.getExtensions().contains(ext)) iconName = "music.png";
        else if (FileCategory.VIDEO.getExtensions().contains(ext)) iconName = "video.png";
        else iconName = "default.png";

        return createImageView(iconName);

    }
    public static ImageView createImageView(String iconName) {
        Image img = iconCache.computeIfAbsent(iconName, name ->
                new Image(IconProvider.class.getResourceAsStream("/icons/" + name), 16, 16, true, true)
        );
        return new ImageView(img);
    }
}

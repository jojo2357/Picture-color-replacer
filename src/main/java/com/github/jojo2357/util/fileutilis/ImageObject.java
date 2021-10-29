package com.github.jojo2357.util.fileutilis;

import com.github.jojo2357.util.Texture;

import java.io.File;

public class ImageObject extends Texture {
    private final File location;

    public ImageObject(File file) {
        super(file.getAbsolutePath());
        location = file;
    }
}

package com.github.jojo2357.util.fileutilis;

import com.github.jojo2357.util.Button;

import java.io.File;

public class FileObject {
    public boolean selected = false;
    public File file;
    public Button button;

    public static FileObject[] fromFiles(File[] files) {
        FileObject[] out = new FileObject[files.length];
        for (int i = 0; i < out.length; i++)
            out[i] = new FileObject(files[i]);
        return out;
    }

    public FileObject(File file) {
        this.file = file;
    }

    public String getRenderName() {
        return file.getName();
    }

    public void attachButton(Button butt) {
        this.button = butt;
    }
}

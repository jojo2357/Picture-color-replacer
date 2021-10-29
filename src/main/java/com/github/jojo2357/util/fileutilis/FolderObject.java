package com.github.jojo2357.util.fileutilis;

import com.github.jojo2357.util.Button;

import java.io.File;

public class FolderObject {
    public boolean selected = false;
    public File file;
    public Button button;
    private final String displayName;

    public static FolderObject[] fromFiles(File[] files) {
        if (files == null)
            return null;
        FolderObject[] out;
        if (files.length > 0 && files[0].getParentFile() != null && files[0].getParentFile().getParentFile() != null && files[0].getParentFile().getParentFile().exists()) {
            out = new FolderObject[files.length + 1];
            out[files.length] = new FolderObject(files[0].getParentFile().getParentFile(), "..");
        } else
            out = new FolderObject[files.length];
        for (int i = 0; i < files.length; i++)
            out[i] = new FolderObject(files[i]);
        return out;
    }

    public FolderObject(File file) {
        this(file, file.getName());
    }

    private FolderObject(File file, String overrideName) {
        this.file = file;
        displayName = overrideName;
    }

    public String getRenderName() {
        return displayName;
    }

    public void attachButton(Button butt) {
        this.button = butt;
    }
}

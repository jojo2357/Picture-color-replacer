package com.github.jojo2357.util.fileutilis;

import java.io.File;

public class FileManager {
    public static FileObject[] getFiles (String dir) {
        return getFiles(dir, "^/.*/..*$");
    }

    public static FileObject[] getFiles (String dir, String matcher) {
        return FileObject.fromFiles(new File(dir).listFiles((fyle) -> fyle.getName().matches(matcher) && !fyle.isDirectory()));
    }

    public static FolderObject[] getFolders (String dir) {
        return FolderObject.fromFiles(new File(dir).listFiles((fyle) -> fyle.isDirectory()));
    }
}

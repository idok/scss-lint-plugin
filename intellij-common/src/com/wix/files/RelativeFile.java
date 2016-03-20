package com.wix.files;

import java.io.File;

public class RelativeFile {
    public final File root;
    public final File file;

    public RelativeFile(File root, File file) {
        this.root = root;
        this.file = file;
    }
}
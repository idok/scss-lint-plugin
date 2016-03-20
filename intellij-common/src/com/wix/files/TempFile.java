package com.wix.files;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiFile;
import com.wix.Util;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class TempFile extends BaseActualFile {
    private static final Logger LOG = Logger.getInstance(Util.LOG_ID);
    private final RelativeFile tempFile;

    TempFile(PsiFile psiFile, File file, RelativeFile tempFile) {
        super(psiFile, file);
        this.tempFile = tempFile;
    }

    @Override
    public File getActualFile() {
        return tempFile.file;
    }

    public RelativeFile getTempFile() {
        return tempFile;
    }

    @Override
    public void deleteTemp() {
        File temp = tempFile.file;
        if (temp != null && temp.exists() && temp.isFile()) {
            boolean isDeleted = temp.delete();
            if (!isDeleted) {
                LOG.debug("Failed to delete temp file");
            }
        }
    }

    @NotNull
    public String getPath() {
        return tempFile.file.getAbsolutePath();
    }

    @NotNull
    @Override
    public String getCwd() {
        return tempFile.root.getAbsolutePath();
    }
}
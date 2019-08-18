package com.wix.files;

import com.intellij.psi.PsiFile;
import com.wix.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Process target file, either the real file or a temp file
 */
public abstract class BaseActualFile {
    BaseActualFile(PsiFile psiFile, File file) {
        this.file = file;
        this.psiFile = psiFile;
    }

    protected final File file;
    private final PsiFile psiFile;

    public File getActualFile() {
        return file;
    }

    public void deleteTemp() {
    }

    @NotNull
    public String getPath() {
        return FileUtils.makeRelative(new File(psiFile.getProject().getBasePath()), file);
    }

    @NotNull
    public String getCwd() {
        return psiFile.getProject().getBasePath();
    }
}

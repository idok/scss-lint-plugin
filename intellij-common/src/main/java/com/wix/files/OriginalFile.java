package com.wix.files;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class OriginalFile extends BaseActualFile {
    OriginalFile(@NotNull PsiFile psiFile, File file) {
        super(psiFile, file);
    }
}
package com.scss.config;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * @author idok
 */
public final class ScssLintConfigFileUtil {
    private ScssLintConfigFileUtil() {
    }

    public static boolean isScssLintConfigFile(PsiFile file) {
        if (file == null) {
            return false;
        }
        if (isScssLintConfigFile(file.getVirtualFile())) {
            return true;
        }
        if (file.getFileType().equals(ScssLintConfigFileType.INSTANCE)) {
            return true;
        }
        return false;
    }

    public static boolean isScssLintConfigFile(PsiElement position) {
        return isScssLintConfigFile(position.getContainingFile().getOriginalFile().getVirtualFile());
    }

    public static boolean isScssLintConfigFile(VirtualFile file) {
        return file != null && file.getExtension() != null &&
                file.getExtension().equals(ScssLintConfigFileType.SCSS_LINT_YML);
    }

}

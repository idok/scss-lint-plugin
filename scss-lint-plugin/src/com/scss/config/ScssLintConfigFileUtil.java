//package com.scss.config;
//
//import com.intellij.openapi.vfs.VirtualFile;
//import com.intellij.psi.PsiElement;
//import com.intellij.psi.PsiFile;
//
///**
// * @author idok
// */
//public final class ScssLintConfigFileUtil {
//    private ScssLintConfigFileUtil() {
//    }
//
//    public static boolean isScssLintConfigFile(PsiFile file) {
//        return file != null && (isScssLintConfigFile(file.getVirtualFile()) || file.getFileType().equals(ScssLintConfigFileType.INSTANCE));
//    }
//
//    public static boolean isScssLintConfigFile(PsiElement position) {
//        return isScssLintConfigFile(position.getContainingFile().getOriginalFile().getVirtualFile());
//    }
//
//    public static boolean isScssLintConfigFile(VirtualFile file) {
//        return file != null && file.getExtension() != null &&
//                file.getExtension().equals(ScssLintConfigFileType.SCSS_LINT_YML);
//    }
//
//}

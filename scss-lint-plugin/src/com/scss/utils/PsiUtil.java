//package com.scss.utils;
//
//import com.intellij.openapi.editor.Document;
//import com.intellij.openapi.util.TextRange;
//import com.intellij.psi.PsiElement;
//import com.intellij.psi.PsiFile;
//import com.scss.utils.scssLint.Lint;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
///**
// * Created by idok on 6/26/14.
// * copied from com.intellij.psi.util.PsiUtilCore to fix compatibility issue with webstorm
// */
//public final class PsiUtil {
//    private PsiUtil() {
//    }
//
//    @NotNull
//    public static PsiElement getElementAtOffset(@NotNull PsiFile file, int offset) {
//        PsiElement elt = file.findElementAt(offset);
//        if (elt == null && offset > 0) {
//            elt = file.findElementAt(offset - 1);
//        }
//        if (elt == null) {
//            return file;
//        }
//        return elt;
//    }
//
//    private static int calcErrorStartOffsetInDocument(@NotNull Document document, int lineStartOffset, int lineEndOffset, int errorColumn, int tabSize) {
//        if (tabSize <= 1) {
//            if (errorColumn < 0) {
//                return lineStartOffset;
//            }
//            if (lineStartOffset + errorColumn <= lineEndOffset) {
//                return lineStartOffset + errorColumn;
//            }
//            return lineEndOffset;
//        }
//        CharSequence docText = document.getCharsSequence();
//        int offset = lineStartOffset;
//        int col = 0;
//        while (offset < lineEndOffset && col < errorColumn) {
//            col += docText.charAt(offset) == '\t' ? tabSize : 1;
//            offset++;
//        }
//        return offset;
//    }
//
//    @Nullable
//    private static TextRange findRangeInDocument(@NotNull PsiFile file, @NotNull Document document, @NotNull Lint.Issue issue,
//                                              int tabSize, boolean showErrorOnWholeLine) {
//        int errorLine = issue.line - 1;
//        int errorColumn = issue.column - 1;
//
//        if (errorLine < 0 || errorLine >= document.getLineCount()) {
//            return null;
//        }
//        int lineEndOffset = document.getLineEndOffset(errorLine);
//        int lineStartOffset = document.getLineStartOffset(errorLine);
//
//        int errorLineStartOffset = calcErrorStartOffsetInDocument(document, lineStartOffset, lineEndOffset, errorColumn, tabSize);
//
//        if (errorLineStartOffset == -1) {
//            return null;
//        }
//        PsiElement element = file.findElementAt(errorLineStartOffset);
////        if (element != null /*&& JSInspection.isSuppressedForStatic(element, getInspectionClass(), inspectionKey.getID())*/)
////            return null;
//        TextRange range;
//        if (showErrorOnWholeLine) {
//            range = new TextRange(lineStartOffset, lineEndOffset);
//        } else {
////            int offset = StringUtil.lineColToOffset(document.getText(), warn.line - 1, warn.column);
//            PsiElement lit = PsiUtil.getElementAtOffset(file, errorLineStartOffset);
//            range = lit.getTextRange();
////            range = new TextRange(errorLineStartOffset, errorLineStartOffset + 1);
//        }
//        return new TextRange(errorLineStartOffset, errorLineStartOffset + issue.length);
//    }
//}

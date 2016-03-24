package com.wix.utils;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Psi Utils
 * Created by idok on 6/26/14.
 */
public final class PsiUtil {
    private PsiUtil() {
    }

    /**
     * copied from com.intellij.psi.util.PsiUtilCore to fix compatibility issue with webstorm
     * @param file psi file
     * @param offset offset to search
     * @return psi element in the offset
     */
    @NotNull
    public static PsiElement getElementAtOffset(@NotNull PsiFile file, int offset) {
        PsiElement elt = file.findElementAt(offset);
        if (elt == null && offset > 0) {
            elt = file.findElementAt(offset - 1);
        }
        if (elt == null) {
            return file;
        }
        return elt;
    }

    public static int calcErrorStartOffsetInDocument(@NotNull Document document, int lineStartOffset, int lineEndOffset, int column, int tabSize) {
        if (tabSize <= 1) {
            if (column < 0) {
                return lineStartOffset;
            }
            if (lineStartOffset + column <= lineEndOffset) {
                return lineStartOffset + column;
            }
            return lineEndOffset;
        }
        CharSequence docText = document.getCharsSequence();
        int offset = lineStartOffset;
        int col = 0;
        while (offset < lineEndOffset && col < column) {
            col += docText.charAt(offset) == '\t' ? tabSize : 1;
            offset++;
        }
        return offset;
    }

    public static int positionToOffset(@NotNull Document document, int line, int column, int tabSize) {
        int errorLine = line - 1;
        int errorColumn = column /*- 1*/;

        if (errorLine < 0 || errorLine >= document.getLineCount()) {
            return -1;
        }
        int lineEndOffset = document.getLineEndOffset(errorLine);
        int lineStartOffset = document.getLineStartOffset(errorLine);

        return calcErrorStartOffsetInDocument(document, lineStartOffset, lineEndOffset, errorColumn, tabSize);
    }

//    @Nullable
//    private static TextRange findRangeInDocument(@NotNull PsiFile file, @NotNull Document document, int line, int column,
//                                              int tabSize, boolean showErrorOnWholeLine) {
//        if (line < 0 || line >= document.getLineCount()) {
//            return null;
//        }
//        int lineEndOffset = document.getLineEndOffset(line);
//        int lineStartOffset = document.getLineStartOffset(line);
//
//        int errorLineStartOffset = calcErrorStartOffsetInDocument(document, lineStartOffset, lineEndOffset, column, tabSize);
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
}

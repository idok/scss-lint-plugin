package com.wix.annotator;

import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author idok
 */
public abstract class LintExternalAnnotator<T> extends ExternalAnnotator<ExternalLintAnnotationInput, ExternalLintAnnotationResult<T>> {

    @Nullable
    @Override
    public ExternalLintAnnotationInput collectInformation(@NotNull PsiFile file) {
        return collectInformation(file, null);
    }

    @Nullable
    @Override
    public ExternalLintAnnotationInput collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
        return collectInformation(file, editor);
    }

    protected abstract ExternalLintAnnotationInput collectInformation(PsiFile file, Editor editor);
}

package com.wix.annotator;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

public class ExternalLintAnnotationInput {
    public final String fileContent;
    public final EditorColorsScheme colorsScheme;
    public final Project project;
    public final PsiFile psiFile;

    public ExternalLintAnnotationInput(Project project, PsiFile psiFile, String fileContent, EditorColorsScheme colorsScheme) {
        this.project = project;
        this.psiFile = psiFile;
        this.fileContent = fileContent;
        this.colorsScheme = colorsScheme;
    }
}
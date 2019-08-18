package com.wix.annotator;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.UnfairLocalInspectionTool;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public abstract class BaseLintInspection extends LocalInspectionTool implements BatchSuppressableTool, UnfairLocalInspectionTool { //extends PropertySuppressableInspectionBase {

    protected abstract ExternalAnnotator getExternalAnnotator();

    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        return ExternalAnnotatorInspectionVisitor.checkFileWithExternalAnnotator(file, manager, isOnTheFly, getExternalAnnotator());
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new ExternalAnnotatorInspectionVisitor(holder, getExternalAnnotator(), isOnTheFly);
    }
}
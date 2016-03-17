package com.scss.annotator;

import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author idok
 */
public abstract class BaseActionFix extends LocalQuickFixAndIntentionActionOnPsiElement implements IntentionAction, HighPriorityAction {
    public BaseActionFix(PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    protected abstract void fix(@NotNull Project project, Editor editor, PsiFile file, PsiElement start);

    public void invoke(@NotNull Project project, @NotNull PsiFile file, @Nullable("is null when called from inspection") Editor editor, @NotNull PsiElement start, @NotNull PsiElement end) {
        fix(project, editor, file, start);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}

package com.scss;

import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.icons.AllIcons.General;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;

import javax.swing.Icon;

import com.scss.settings.ScssLintSettingsPage;
import org.jetbrains.annotations.NotNull;

public class EditSettingsAction implements IntentionAction, Iconable, HighPriorityAction {
    private static boolean ourInvoked = false;
    private final ScssLintSettingsPage myLinterConfigurable;
    private final boolean myForFileLevelAnnotation;
    private final Icon myIcon;

    public EditSettingsAction(@NotNull ScssLintSettingsPage linterConfigurable) {
        this(linterConfigurable, false, General.Settings);
    }

    public EditSettingsAction(@NotNull ScssLintSettingsPage linterConfigurable, @NotNull Icon icon) {
        this(linterConfigurable, false, icon);
    }

    public EditSettingsAction(@NotNull ScssLintSettingsPage configurable, boolean forFileLevelAnnotation) {
        this(configurable, forFileLevelAnnotation, General.Settings);
    }

    public EditSettingsAction(@NotNull ScssLintSettingsPage linterConfigurable, boolean forFileLevelAnnotation, @NotNull Icon icon) {
        this.myLinterConfigurable = linterConfigurable;
        this.myForFileLevelAnnotation = forFileLevelAnnotation;
        this.myIcon = icon;
    }

    public Icon getIcon(@IconFlags int flags) {
        return this.myIcon;
    }

    @NotNull
    public String getText() {
        return this.myForFileLevelAnnotation ? "Settings..." : this.myLinterConfigurable.getDisplayName() + " settings...";
    }

    @NotNull
    public String getFamilyName() {
        return this.getText();
    }

    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if (!ourInvoked) {
            ourInvoked = true;
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                public void run() {
                    EditSettingsAction.ourInvoked = false;
                    EditSettingsAction.this.myLinterConfigurable.showSettings();
                }
            }, ModalityState.any());
        }

    }

    public boolean startInWriteAction() {
        return false;
    }
}

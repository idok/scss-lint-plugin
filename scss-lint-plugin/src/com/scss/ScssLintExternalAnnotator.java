package com.scss;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInsight.daemon.impl.SeverityRegistrar;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.profile.codeInspection.InspectionProjectProfileManager;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.scss.annotator.BaseActionFix;
import com.scss.annotator.Fixes;
import com.scss.config.ScssLintConfigFileChangeTracker;
import com.scss.settings.ScssLintSettingsPage;
import com.scss.utils.ScssLintRunner;
import com.scss.utils.scssLint.Lint;
import com.scss.utils.scssLint.LintResult;
import com.wix.ThreadLocalActualFile;
import com.wix.annotator.AnnotatorUtils;
import com.wix.files.ActualFileManager;
import com.wix.files.BaseActualFile;
import com.wix.files.ThreadLocalTempActualFile;
import com.wix.utils.PsiUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//import org.jetbrains.plugins.scss.SCSSFileType;
//import org.jetbrains.plugins.scss.psi.SCSSFile;

/**
 * @author idok
 */
public class ScssLintExternalAnnotator extends ExternalAnnotator<ScssLintAnnotationInput, ScssLintAnnotationResult> {

    public static final ScssLintExternalAnnotator INSTANCE = new ScssLintExternalAnnotator();
    private static final Logger LOG = Logger.getInstance(ScssLintBundle.LOG_ID);
    private static final Key<ThreadLocalActualFile> SCSS_TEMP_FILE_KEY = Key.create("SCSS_TEMP_FILE");
    public static final String SCSS = "scss";

    @Nullable
    @Override
    public ScssLintAnnotationInput collectInformation(@NotNull PsiFile file) {
        return collectInformation(file, null);
    }

    @Nullable
    @Override
    public ScssLintAnnotationInput collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
        return collectInformation(file, editor);
    }

    @NotNull
    public static HighlightDisplayKey getHighlightDisplayKeyByClass() {
        String id = "ScssLint";
        HighlightDisplayKey key = HighlightDisplayKey.find(id);
        if (key == null) {
            key = new HighlightDisplayKey(id, id);
        }
        return key;
    }

    @Override
    public void apply(@NotNull PsiFile file, ScssLintAnnotationResult annotationResult, @NotNull AnnotationHolder holder) {
        if (annotationResult == null) {
            return;
        }
        InspectionProjectProfileManager inspectionProjectProfileManager = InspectionProjectProfileManager.getInstance(file.getProject());
        SeverityRegistrar severityRegistrar = inspectionProjectProfileManager.getSeverityRegistrar();
        HighlightDisplayKey inspectionKey = getHighlightDisplayKeyByClass();
        EditorColorsScheme colorsScheme = annotationResult.input.colorsScheme;

        Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
        if (document == null) {
            return;
        }

        if (annotationResult.fileLevel != null) {
            Annotation annotation = holder.createWarningAnnotation(file, annotationResult.fileLevel);
            annotation.registerFix(new EditSettingsAction(new ScssLintSettingsPage(file.getProject())));
            annotation.setFileLevelAnnotation(true);
            return;
        }

        // TODO consider adding a fix to edit configuration file
        if (annotationResult.result == null || annotationResult.result.lint == null || annotationResult.result.lint.isEmpty()) {
            return;
        }
        // String relativeFile = FileUtils.makeRelative(file.getProject(), file.getVirtualFile());
        List<Lint.Issue> issues = annotationResult.result.lint.values().iterator().next();
        if (issues == null) {
            return;
        }
        ScssLintProjectComponent component = annotationResult.input.project.getComponent(ScssLintProjectComponent.class);
        int tabSize = 4;
        for (Lint.Issue issue : issues) {
            HighlightSeverity severity = getHighlightSeverity(issue, component.treatAsWarnings);
            TextAttributes forcedTextAttributes = AnnotatorUtils.getTextAttributes(colorsScheme, severityRegistrar, severity);
            Annotation annotation = createAnnotation(holder, file, document, issue, "SCSS Lint: ", tabSize, severity, forcedTextAttributes, inspectionKey, false);
            if (annotation != null) {
                int offset = StringUtil.lineColToOffset(document.getText(), issue.line - 1, issue.column);
                PsiElement lit = PsiUtil.getElementAtOffset(file, offset);
                BaseActionFix actionFix = Fixes.getFixForRule(issue.linter, lit);
                if (actionFix != null) {
                    annotation.registerFix(actionFix, null, inspectionKey);
                }
//                annotation.registerFix(new SuppressActionFix(issue.rule, lit), null, inspectionKey);
            }
        }
    }

    private static HighlightSeverity getHighlightSeverity(Lint.Issue warn) {
        return warn.severity.equals("error") ? HighlightSeverity.ERROR : HighlightSeverity.WARNING;
    }

    private static HighlightSeverity getHighlightSeverity(Lint.Issue issue, boolean treatAsWarnings) {
        return treatAsWarnings ? HighlightSeverity.WARNING : getHighlightSeverity(issue);
    }

    @Nullable
    private static Annotation createAnnotation(@NotNull AnnotationHolder holder, @NotNull PsiFile file, @NotNull Document document, @NotNull Lint.Issue issue,
                                               @NotNull String messagePrefix, int tabSize, @NotNull HighlightSeverity severity, @Nullable TextAttributes forcedTextAttributes,
                                               @NotNull HighlightDisplayKey inspectionKey, boolean showErrorOnWholeLine) {
        int errorLine = issue.line - 1;
        int errorColumn = issue.column - 1;

        if (errorLine < 0 || errorLine >= document.getLineCount()) {
            return null;
        }
        int lineEndOffset = document.getLineEndOffset(errorLine);
        int lineStartOffset = document.getLineStartOffset(errorLine);

        int errorLineStartOffset = PsiUtil.calcErrorStartOffsetInDocument(document, lineStartOffset, lineEndOffset, errorColumn, tabSize);

        if (errorLineStartOffset == -1) {
            return null;
        }
        PsiElement element = file.findElementAt(errorLineStartOffset);
//        if (element != null /*&& JSInspection.isSuppressedForStatic(element, getInspectionClass(), inspectionKey.getID())*/)
//            return null;
        TextRange range;
        if (showErrorOnWholeLine) {
            range = new TextRange(lineStartOffset, lineEndOffset);
        } else {
//            int offset = StringUtil.lineColToOffset(document.getText(), warn.line - 1, warn.column);
            PsiElement lit = PsiUtil.getElementAtOffset(file, errorLineStartOffset);
            range = lit.getTextRange();
//            range = new TextRange(errorLineStartOffset, errorLineStartOffset + 1);
        }
        range = new TextRange(errorLineStartOffset, errorLineStartOffset + issue.length);

        Annotation annotation = createAnnotation(holder, severity, forcedTextAttributes, range, messagePrefix + issue.reason.trim() + " (" + (issue.linter == null ? "none" : issue.linter) + ')');
        if (annotation != null) {
            annotation.setAfterEndOfLine(errorLineStartOffset == lineEndOffset);
        }
        return annotation;
    }

    @NotNull
    public static Annotation createAnnotation(@NotNull AnnotationHolder holder, @NotNull HighlightSeverity severity, @NotNull TextRange range, @NotNull String message) {
        /*
          avoid using
          holder.createAnnotation(severity, range, message); as it is not supported in PhpStorm: 7.1.3 (PS-133.982)
          https://github.com/idok/scss-lint-plugin/issues/5
         */
        if (severity == HighlightSeverity.ERROR) {
            return holder.createErrorAnnotation(range, message);
        }
        return holder.createWarningAnnotation(range, message);
    }

    @Nullable
    public static Annotation createAnnotation(@NotNull AnnotationHolder holder, @NotNull HighlightSeverity severity, @Nullable TextAttributes forcedTextAttributes, @NotNull TextRange range, @NotNull String message) {
        if (forcedTextAttributes != null) {
            Annotation annotation = createAnnotation(holder, severity, range, message);
            annotation.setEnforcedTextAttributes(forcedTextAttributes);
            return annotation;
        }
        return createAnnotation(holder, severity, range, message);
    }

    @Nullable
    private static ScssLintAnnotationInput collectInformation(@NotNull PsiFile psiFile, @Nullable Editor editor) {
        if (psiFile.getContext() != null || !isScssFile(psiFile)) {
            return null;
        }
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null || !virtualFile.isInLocalFileSystem()) {
            return null;
        }
        if (psiFile.getViewProvider() instanceof MultiplePsiFilesPerDocumentFileViewProvider) {
            return null;
        }
        Project project = psiFile.getProject();
//        ScssLintProjectComponent component = project.getComponent(ScssLintProjectComponent.class);
//        if (!component.isSettingsValid() || !component.isEnabled()) {
//            return new ScssLintAnnotationInput(project, psiFile, null, null, "Invalid settings!");
//        }
        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        if (document == null) {
            return null;
        }
        String fileContent = document.getText();
        if (StringUtil.isEmptyOrSpaces(fileContent)) {
            return null;
        }
        EditorColorsScheme colorsScheme = editor == null ? null : editor.getColorsScheme();
        return new ScssLintAnnotationInput(project, psiFile, fileContent, colorsScheme);
    }

    private static boolean isScssFile(PsiFile file) {
        return file.getVirtualFile().getExtension().equals(SCSS);
//        return file instanceof SCSSFile && file.getFileType().equals(SCSSFileType.SCSS);
    }

    private static final Key<ThreadLocalTempActualFile> TEMP_FILE = Key.create("SCSS_LINT_TEMP_FILE");

    @Nullable
    @Override
    public ScssLintAnnotationResult doAnnotate(ScssLintAnnotationInput collectedInfo) {
        BaseActualFile actualCodeFile = null;
        try {
            PsiFile file = collectedInfo.psiFile;
            if (!isScssFile(file)) {
                return null;
            }
            ScssLintProjectComponent component = file.getProject().getComponent(ScssLintProjectComponent.class);
            if (!component.isEnabled()) {
                return new ScssLintAnnotationResult(collectedInfo, null, "SCSS Lint is available for this file but is not configured");
            }
            if (!component.isSettingsValid()) {
                return new ScssLintAnnotationResult(collectedInfo, null, "SCSS Lint is not configured correctly");
            }

            ScssLintConfigFileChangeTracker.getInstance(collectedInfo.project).startIfNeeded();
            actualCodeFile = ActualFileManager.getOrCreateActualFile(TEMP_FILE, file, collectedInfo.fileContent);
            if (actualCodeFile == null) {
                LOG.warn("Failed to create file for lint");
                return null;
            }
            LintResult result = ScssLintRunner.runLint(actualCodeFile.getCwd(), actualCodeFile.getPath(), component.scssLintExecutable, component.scssLintConfigFile);

            if (StringUtils.isNotEmpty(result.errorOutput)) {
                component.showInfoNotification(result.errorOutput, NotificationType.WARNING);
                return null;
            }
            Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
            if (document == null) {
                component.showInfoNotification("Error running SCSS Lint inspection: Could not get document for file " + file.getName(), NotificationType.WARNING);
                LOG.warn("Could not get document for file " + file.getName());
                return null;
            }
            return new ScssLintAnnotationResult(collectedInfo, result);
        } catch (Exception e) {
            LOG.error("Error running ScssLint inspection: ", e);
            ScssLintProjectComponent.showNotification("Error running SCSS Lint inspection: " + e.getMessage(), NotificationType.ERROR);
        } finally {
            ActualFileManager.dispose(actualCodeFile);
        }
        return null;
    }
}

class ScssLintAnnotationInput {
    public final String fileContent;
    public final EditorColorsScheme colorsScheme;
    public final Project project;
    public final PsiFile psiFile;

    public ScssLintAnnotationInput(Project project, PsiFile psiFile, String fileContent, EditorColorsScheme colorsScheme) {
        this.project = project;
        this.psiFile = psiFile;
        this.fileContent = fileContent;
        this.colorsScheme = colorsScheme;
    }
}

class ScssLintAnnotationResult {
    public ScssLintAnnotationResult(ScssLintAnnotationInput input, LintResult result) {
        this.input = input;
        this.result = result;
    }

    public ScssLintAnnotationResult(ScssLintAnnotationInput input, LintResult result, String fileLevel) {
        this.input = input;
        this.result = result;
        this.fileLevel = fileLevel;
    }

    public final ScssLintAnnotationInput input;
    public final LintResult result;
    public String fileLevel;
}

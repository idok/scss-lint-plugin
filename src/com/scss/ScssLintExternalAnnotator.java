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
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.profile.codeInspection.InspectionProjectProfileManager;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.scss.utils.FileUtils;
import com.scss.utils.PsiUtil;
import com.scss.utils.ScssLintRunner;
import com.scss.utils.scssLint.Lint;
import com.scss.utils.scssLint.LintResult;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scss.SCSSFileType;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author idok
 */
public class ScssLintExternalAnnotator extends ExternalAnnotator<ScssLintAnnotationInput, ScssLintAnnotationResult> {

    public static final ScssLintExternalAnnotator INSTANCE = new ScssLintExternalAnnotator();
    private static final Logger LOG = Logger.getInstance(ScssLintBundle.LOG_ID);

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

        // TODO consider adding a fix to edit configuration file
        List<Lint.Issue> issues = annotationResult.result.lint.file.issues;
        ScssLintProjectComponent component = annotationResult.input.project.getComponent(ScssLintProjectComponent.class);
        int tabSize = 4;
        for (Lint.Issue issue : issues) {
            HighlightSeverity severity = getHighlightSeverity(issue, component.treatAsWarnings);
//            TextAttributes forcedTextAttributes = JSLinterUtil.getTextAttributes(colorsScheme, severityRegistrar, severity);
            TextAttributes forcedTextAttributes = null;
            Annotation annotation = createAnnotation(holder, file, document, issue, "SCSS Lint: ", tabSize, severity, forcedTextAttributes, inspectionKey, false);
            if (annotation != null) {
                int offset = StringUtil.lineColToOffset(document.getText(), issue.line - 1, issue.column);
                PsiElement lit = PsiUtil.getElementAtOffset(file, offset);
//                BaseActionFix actionFix = Fixes.getFixForRule(issue.rule, lit);
//                if (actionFix != null) {
//                    annotation.registerFix(actionFix, null, inspectionKey);
//                }
//                annotation.registerFix(new SuppressActionFix(issue.rule, lit), null, inspectionKey);
            }
        }
    }

    private static HighlightSeverity getHighlightSeverity(Lint.Issue warn) {
        return warn.severity.equals("error") ? HighlightSeverity.ERROR : HighlightSeverity.WARNING;
    }

    private static HighlightSeverity getHighlightSeverity(Lint.Issue issue, boolean treatAsWarnings) {
        if (treatAsWarnings) {
            return HighlightSeverity.WARNING;
        }
        return getHighlightSeverity(issue);
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

        int errorLineStartOffset = calcErrorStartOffsetInDocument(document, lineStartOffset, lineEndOffset, errorColumn, tabSize);

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

        Annotation annotation = createAnnotation(holder, severity, forcedTextAttributes, range, messagePrefix + issue.reason.trim() + " (" + issue.linter + ')');
        if (annotation != null) {
            annotation.setAfterEndOfLine(errorLineStartOffset == lineEndOffset);
        }
        return annotation;
    }

    @Nullable
    public static Annotation createAnnotation(@NotNull AnnotationHolder holder, @NotNull HighlightSeverity severity, @Nullable TextAttributes forcedTextAttributes, @NotNull TextRange range, @NotNull String message) {
        if (forcedTextAttributes != null) {
            Annotation annotation = holder.createAnnotation(severity, range, message);
//            annotation.setEnforcedTextAttributes(forcedTextAttributes);
            return annotation;
        }
        if (severity == HighlightSeverity.ERROR) {
            return holder.createErrorAnnotation(range, message);
        }
        return holder.createWarningAnnotation(range, message);
    }

    private static int calcErrorStartOffsetInDocument(@NotNull Document document, int lineStartOffset, int lineEndOffset, int errorColumn, int tabSize) {
        if (tabSize <= 1) {
            if (errorColumn < 0) {
                return lineStartOffset;
            }
            if (lineStartOffset + errorColumn <= lineEndOffset) {
                return lineStartOffset + errorColumn;
            }
            return lineEndOffset;
        }
        CharSequence docText = document.getCharsSequence();
        int offset = lineStartOffset;
        int col = 0;
        while (offset < lineEndOffset && col < errorColumn) {
            col += docText.charAt(offset) == '\t' ? tabSize : 1;
            offset++;
        }
        return offset;
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
        ScssLintProjectComponent component = project.getComponent(ScssLintProjectComponent.class);
        if (!component.isSettingsValid() || !component.isEnabled()) {
            return null;
        }
        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        if (document == null) {
            return null;
        }
        String fileContent = document.getText();
        if (StringUtil.isEmptyOrSpaces(fileContent)) {
            return null;
        }
        EditorColorsScheme colorsScheme = editor != null ? editor.getColorsScheme() : null;
        return new ScssLintAnnotationInput(project, psiFile, fileContent, colorsScheme);
    }

    private static boolean isScssFile(PsiFile file) {
        return file.getVirtualFile().getExtension().equals(SCSSFileType.DEFAULT_EXTENSION);
//        return file instanceof SCSSFile && file.getFileType().equals(SCSSFileType.SCSS);
//        return file.getFileType().equals(SCSSFileType.SCSS);
    }

    private static final Key<ThreadLocalActualFile> SCSS_LINT_TEMP_FILE_KEY = Key.create("SCSS_LINT_TEMP_FILE_KEY");

    @Nullable
    private static ActualFile getOrCreateActualFile(@NotNull Key<ThreadLocalActualFile> key, @NotNull VirtualFile virtualFile, @Nullable String content) {
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        if (!fileDocumentManager.isFileModified(virtualFile)) {
            File file = new File(virtualFile.getPath());
            if (file.isFile()) {
                return new ActualFile(file);
            }
        }
        ThreadLocalActualFile threadLocal = key.get(virtualFile);
        if (threadLocal == null) {
            threadLocal = virtualFile.putUserDataIfAbsent(key, new ThreadLocalActualFile(virtualFile));
        }
        File file = threadLocal.getFile();
        if (file == null) {
            return null;
        }
        if (content == null) {
            Document document = fileDocumentManager.getDocument(virtualFile);
            if (document != null) {
                content = document.getText();
            }
        }
        if (content == null) {
            return null;
        }
        try {
            FileUtil.writeToFile(file, content);
            return new ActualFile(file, threadLocal.isTemp);
        } catch (IOException e) {
            LOG.warn("Can not write to " + file.getAbsolutePath(), e);
        }
        return null;
    }

    static class ActualFile {
        ActualFile(File file, boolean isTemp) {
            this.file = file;
            this.isTemp = isTemp;
        }

        ActualFile(File file) {
            this(file, false);
        }

        File file;
        boolean isTemp;
    }

    @Nullable
    @Override
    public ScssLintAnnotationResult doAnnotate(ScssLintAnnotationInput collectedInfo) {
        try {
            String f = com.wix.Util.f();

            PsiFile file = collectedInfo.psiFile;
            if (!isScssFile(file)) return null;
            ScssLintProjectComponent component = file.getProject().getComponent(ScssLintProjectComponent.class);
            if (!component.isSettingsValid() || !component.isEnabled()) {
                return null;
            }

//            ScssLintConfigFileChangeTracker.getInstance(collectedInfo.project).startIfNeeded();
            String relativeFile;
            ActualFile actualCodeFile = getOrCreateActualFile(SCSS_LINT_TEMP_FILE_KEY, file.getVirtualFile(), collectedInfo.fileContent);
            if (actualCodeFile == null || actualCodeFile.file == null) {
                return null;
            }
            relativeFile = FileUtils.makeRelative(new File(file.getProject().getBasePath()), actualCodeFile.file);
            LintResult result = ScssLintRunner.runLint(file.getProject().getBasePath(), relativeFile, component.scssLintExecutable, component.scssLintConfigFile);
            // , component.nodeInterpreter, component.scssLintExecutable, component.scssLintConfigFile, component.rulesPath);

            if (actualCodeFile.isTemp) {
                boolean isDeleted = actualCodeFile.file.delete();
                if (!isDeleted) {
                    LOG.debug("Failed to delete temp file");
                }
            }
            if (StringUtils.isNotEmpty(result.errorOutput)) {
                component.showInfoNotification(result.errorOutput, NotificationType.WARNING);
                return null;
            }
            Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
            if (document == null) {
                component.showInfoNotification("Error running SCSS Lint inspection: Could not get document for file " + file.getName(), NotificationType.WARNING);
                System.out.println("Could not get document for file " + file.getName());
                return null;
            }
            return new ScssLintAnnotationResult(collectedInfo, result);
        } catch (Exception e) {
            LOG.error("Error running ScssLint inspection: ", e);
            ScssLintProjectComponent.showNotification("Error running SCSS Lint inspection: " + e.getMessage(), NotificationType.ERROR);
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

    public final ScssLintAnnotationInput input;
    public final LintResult result;
}

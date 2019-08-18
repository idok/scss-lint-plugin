package com.scss.config;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorEventMulticaster;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.scss.ScssLintProjectComponent;
import com.scss.utils.ScssLintFinder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class ScssLintConfigFileChangeTracker {
    private final AtomicBoolean TRACKING = new AtomicBoolean(false);
    private final Project project;

    public static final String SCSS_LINT_YML = "." + ScssLintFinder.SCSS_LINT_YML;

    public ScssLintConfigFileChangeTracker(@NotNull Project project) {
        this.project = project;
    }

    @NotNull
    public static ScssLintConfigFileChangeTracker getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ScssLintConfigFileChangeTracker.class);
    }

    public void startIfNeeded() {
        if (TRACKING.compareAndSet(false, true))
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                public void run() {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        public void run() {
                            VirtualFileManager.getInstance().addVirtualFileListener(new ScssLintConfigFileVfsListener(), ScssLintConfigFileChangeTracker.this.project);
                            EditorEventMulticaster multicaster = EditorFactory.getInstance().getEventMulticaster();
                            multicaster.addDocumentListener(new ScssLintConfigFileDocumentListener(), ScssLintConfigFileChangeTracker.this.project);
                        }
                    });
                }
            });
    }

    private void onChange(@NotNull VirtualFile file) {
//        if (file.getFileType().equals(ScssLintConfigFileType.INSTANCE) && !project.isDisposed()) {
        if (SCSS_LINT_YML.equals(file.getName()) && !project.isDisposed()) {
            restartCodeAnalyzerIfNeeded();
        }
    }

    private void restartCodeAnalyzerIfNeeded() {
        ScssLintProjectComponent component = project.getComponent(ScssLintProjectComponent.class);
        if (component.isEnabled()) {
            DaemonCodeAnalyzer.getInstance(project).restart();
        }
    }

    private final class ScssLintConfigFileDocumentListener implements DocumentListener {
        private ScssLintConfigFileDocumentListener() {
        }

        public void beforeDocumentChange(@NotNull DocumentEvent event) {
        }

        public void documentChanged(@NotNull DocumentEvent event) {
            VirtualFile file = FileDocumentManager.getInstance().getFile(event.getDocument());
            if (file != null) {
                ScssLintConfigFileChangeTracker.this.onChange(file);
            }
        }
    }

    private final class ScssLintConfigFileVfsListener implements VirtualFileListener {
        @Contract(pure = true)
        private ScssLintConfigFileVfsListener() {
        }

        public void fileCreated(@NotNull VirtualFileEvent event) {
            ScssLintConfigFileChangeTracker.this.onChange(event.getFile());
        }

        public void fileDeleted(@NotNull VirtualFileEvent event) {
            ScssLintConfigFileChangeTracker.this.onChange(event.getFile());
        }

        public void fileMoved(@NotNull VirtualFileMoveEvent event) {
            ScssLintConfigFileChangeTracker.this.onChange(event.getFile());
        }

        public void fileCopied(@NotNull VirtualFileCopyEvent event) {
            ScssLintConfigFileChangeTracker.this.onChange(event.getFile());
            ScssLintConfigFileChangeTracker.this.onChange(event.getOriginalFile());
        }
    }
}


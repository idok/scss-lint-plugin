package com.scss;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Process target file, either the real file or a temp file
 */
public class ActualFile {
    private static final Logger LOG = Logger.getInstance(ScssLintBundle.LOG_ID);

    private ActualFile(File file, boolean isTemp) {
        this.file = file;
        this.isTemp = isTemp;
    }

    private ActualFile(File file) {
        this(file, false);
    }

    private File file;
    private boolean isTemp;

    File getFile() {
        return file;
    }

    void deleteTemp() {
        if (isTemp) {
            boolean isDeleted = file.delete();
            if (!isDeleted) {
                LOG.debug("Failed to delete temp file");
            }
        }
    }

    private static final Key<ThreadLocalActualFile> SCSS_LINT_TEMP_FILE_KEY = Key.create("SCSS_LINT_TEMP_FILE_KEY");

    @Nullable
    static ActualFile getOrCreateActualFile(@NotNull VirtualFile virtualFile, @Nullable String content) {
        return getOrCreateActualFile(SCSS_LINT_TEMP_FILE_KEY, virtualFile, content);
    }

    @Nullable
    static ActualFile getOrCreateActualFile(@NotNull Key<ThreadLocalActualFile> key, @NotNull VirtualFile virtualFile, @Nullable String content) {
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
}

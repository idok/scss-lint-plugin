package com.wix;

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
    private static final Logger LOG = Logger.getInstance(Util.LOG_ID);

    ActualFile(File file, File tempFile) {
        this.file = file;
        this.tempFile = tempFile;
    }

    ActualFile(File file) {
        this(file, null);
    }

    private final File file;
    private final File tempFile;

    public File getFile() {
        return file;
    }

    public File getActualFile() {
        if (tempFile != null) {
            return tempFile;
        }
        return file;
    }

    public void deleteTemp() {
        if (tempFile != null && tempFile.exists() && tempFile.isFile()) {
            boolean isDeleted = tempFile.delete();
            if (!isDeleted) {
                LOG.debug("Failed to delete temp file");
            }
        }
    }

    @Nullable
    public static ActualFile getOrCreateActualFile(@NotNull Key<ThreadLocalActualFile> key, @NotNull VirtualFile virtualFile, @Nullable String content) {
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
        File file = threadLocal.getOrCreateFile();
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
            return new ActualFile(new File(virtualFile.getPath()), file);
        } catch (IOException e) {
            LOG.warn("Can not write to " + file.getAbsolutePath(), e);
        }
        return null;
    }
}

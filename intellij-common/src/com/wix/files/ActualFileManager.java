package com.wix.files;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.wix.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public final class ActualFileManager {
    private static final Logger LOG = Logger.getInstance(Util.LOG_ID);

    private ActualFileManager() {
    }

    @Nullable
    public static BaseActualFile getOrCreateActualFile(@NotNull Key<ThreadLocalTempActualFile> key, @NotNull PsiFile psiFile, @Nullable String content) {
        // Original file
        VirtualFile virtualFile = psiFile.getVirtualFile();
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        if (!fileDocumentManager.isFileModified(virtualFile)) {
            File file = new File(virtualFile.getPath());
            if (file.isFile()) {
                return new OriginalFile(psiFile, file);
            }
        }

        // TEMP File
        ThreadLocalTempActualFile threadLocal = key.get(virtualFile);
        if (threadLocal == null) {
            threadLocal = virtualFile.putUserDataIfAbsent(key, new ThreadLocalTempActualFile("scss-temp", psiFile));
        }
        RelativeFile file = threadLocal.getOrCreateFile();
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
            FileUtil.writeToFile(file.file, content);
            return new TempFile(psiFile, new File(virtualFile.getPath()), file);
        } catch (IOException e) {
            LOG.warn("Can not write to " + file.file.getAbsolutePath(), e);
        }
        return null;
    }

    public static void dispose(BaseActualFile actualCodeFile) {
        if (actualCodeFile != null) {
            actualCodeFile.deleteTemp();
        }
    }
}

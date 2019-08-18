package com.wix;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.wix.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Lint target file thread local storage
 */
public class ThreadLocalActualFile extends ThreadLocal<String> {
    private final String baseName;
    private final String extension;
    private final VirtualFile originalFile;
    private File tempFile;
    private static final Logger LOG = Logger.getInstance(Util.LOG_ID);

    public boolean isTemp;

    private static final String SCSS_LINT_TMP = "_scsslint_tmp";
    private static final String TEMP_DIR_NAME = "intellij-scsslint-temp";

    public ThreadLocalActualFile(@NotNull VirtualFile originalFile) {
        this.baseName = originalFile.getNameWithoutExtension();
        this.extension = FileUtils.getExtensionWithDot(originalFile);
        this.originalFile = originalFile;
    }

    public VirtualFile getFile() {
        return originalFile;
    }

    @Nullable
    public File getOrCreateFile() {
        String path = super.get();
        if (path != null) {
            File file = new File(path);
            if (file.isFile()) {
                return file;
            }
        }
        File file = createFile();
        if (file != null) {
            set(file.getAbsolutePath());
            tempFile = file;
            return file;
        }
        return null;
    }

    @Nullable
    public static File getOrCreateTempDir() {
        File tmpDir = new File(FileUtil.getTempDirectory());
        File dir = new File(tmpDir, TEMP_DIR_NAME);
        if (dir.isDirectory() || dir.mkdirs()) {
            return dir;
        }
        try {
            return FileUtil.createTempDirectory(tmpDir, TEMP_DIR_NAME, null);
        } catch (IOException ignored) {
            LOG.warn("Can't create '" + TEMP_DIR_NAME + "' temporary directory.");
        }
        return null;
    }

    private File createFileAsSibling() {
        File retFile;
        try {
            // try to create a temp file next to original file
            retFile = File.createTempFile(this.baseName + SCSS_LINT_TMP, this.extension, new File(originalFile.getParent().getPath()));
            isTemp = true;
            return retFile;
        } catch (IOException e) {
            LOG.warn("Can not create temp file", e);
        }
        return null;
    }

    @Nullable
    private File createFile() {
//        File retFile = new File(file.getParent().getPath(), file.getNameWithoutExtension() + "_jscs_tmp." + file.getExtension());
        File retFile = createFileAsSibling();
        if (retFile != null) {
            return retFile;
        }

        // try to create a temp file in temp folder
        File dir = getOrCreateTempDir();
        if (dir == null) {
            return null;
        }
        File file = new File(dir, this.baseName + this.extension);
        boolean created = false;
        if (!file.exists()) {
            try {
                created = file.createNewFile();
            } catch (IOException ignored) {
                LOG.warn("Can not create " + file.getAbsolutePath());
            }
        }
        if (!created) {
            try {
                file = FileUtil.createTempFile(dir, this.baseName, this.extension);
            } catch (IOException e) {
                LOG.warn("Can not create temp file", e);
                return null;
            }
        }
        file.deleteOnExit();
        return file;
    }
}

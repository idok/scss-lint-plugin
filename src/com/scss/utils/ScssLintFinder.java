package com.scss.utils;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.scss.config.ScssLintConfigFileType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Set;

public final class ScssLintFinder {
    public static final String SCSS_LINT_BASE_NAME = SystemInfo.isWindows ? "scss-lint.bat" : "scss-lint";

    private ScssLintFinder() {
    }

    @NotNull
    public static List<File> findAllScssLintExe() {
        Set<File> exes = ContainerUtil.newLinkedHashSet();
        // TODO looks like on windows it only searches system path and not user's
        List<File> fromPath = PathEnvironmentVariableUtil.findAllExeFilesInPath(SCSS_LINT_BASE_NAME);
        exes.addAll(fromPath);
        return ContainerUtil.newArrayList(exes);
    }

    /**
     * find possible scss-lint config files
     * @param projectRoot project root
     * @return a list of scss-lint config files
     */
    public static List<String> searchForLintConfigFiles(final File projectRoot) {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.equals(ScssLintConfigFileType.SCSS_LINT_YML);
            }
        };
        List<String> files = FileUtils.recursiveVisitor(projectRoot, filter);
        return ContainerUtil.map(files, new Function<String, String>() {
            public String fun(String curFile) {
                return FileUtils.makeRelative(projectRoot, new File(curFile));
            }
        });
    }
}
package com.scss.utils;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.util.containers.ContainerUtil;
import com.wix.nodejs.NodeFinder;
import com.wix.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ScssLintFinder {
    public static final String SCSS_LINT_BASE_NAME = NodeFinder.getBinName("scss-lint");
    public static final String SCSS_LINT_YML = "scss-lint.yml";

    private ScssLintFinder() {
    }

    @NotNull
    public static List<File> findAllScssLintExe() {
        // TODO looks like on windows it only searches system path and not user's
        List<File> fromPath = PathEnvironmentVariableUtil.findAllExeFilesInPath(SCSS_LINT_BASE_NAME);
        Set<File> exes = new LinkedHashSet<>(fromPath);
        return ContainerUtil.newArrayList(exes);
    }

    /**
     * find possible scss-lint config files
     * @param projectRoot project root
     * @return a list of scss-lint config files
     */
    public static List<String> searchForLintConfigFiles(final File projectRoot) {
        FilenameFilter filter = (file, name) -> name.equals(SCSS_LINT_YML);
        List<String> files = FileUtils.recursiveVisitor(projectRoot, filter);
        return ContainerUtil.map(files, curFile -> FileUtils.makeRelative(projectRoot, new File(curFile)));
    }
}
package com.scsslint.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.scss.utils.ScssLintFinder;
import com.scss.utils.ScssLintRunner;
import com.scss.utils.scssLint.LintResult;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static com.scsslint.utils.Settings.*;

public class ScssLintRunner2Test {
    private static final String CONFIG = "";

    private static ScssLintRunner.ScssLintSettings createSettings(String targetFile) {
        return ScssLintRunner.buildSettings(PLUGIN_ROOT, targetFile, SCSS_EXE, CONFIG);
    }

    private static ScssLintRunner.ScssLintSettings createSettings() {
        return createSettings("");
    }

    @Test
    public void testLint() {
        String scssFile = "testData/one.scss";
        ScssLintRunner.ScssLintSettings settings = createSettings(scssFile);
        LintResult result = ScssLintRunner.runLint(settings.cwd, settings.targetFile, SCSS_EXE, CONFIG);
        assertEquals("should have 1 issue", 1, result.lint.get(scssFile).size());
        assertEquals("should have props warn", "Properties should be ordered color, font", result.lint.get(scssFile).get(0).reason);
    }

    @Test
    public void findExeInPath() {
        List<File> fromPath = PathEnvironmentVariableUtil.findAllExeFilesInPath(ScssLintFinder.SCSS_LINT_BASE_NAME);
        System.out.println(fromPath);
        assertEquals("should find exe", "/usr/local/bin/scss-lint", fromPath.get(0).toString());
    }

    @Test
    public void testVersion() {
        ScssLintRunner.ScssLintSettings settings = createSettings();
        try {
            String version = ScssLintRunner.runVersion(settings);
            assertEquals("version should be", "scss-lint 0.38.0", version);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

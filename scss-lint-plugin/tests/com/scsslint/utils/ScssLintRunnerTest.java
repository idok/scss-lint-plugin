package com.scsslint.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.execution.process.ProcessOutput;
import com.scss.utils.ScssLintFinder;
import com.scss.utils.ScssLintRunner;
import com.scss.utils.scssLint.LintResult;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScssLintRunnerTest {

    public static final String SCSS_EXE = "/usr/bin/scss-lint";
    public static final String PLUGIN_ROOT = "/Users/idok/Projects/scss-lint-plugin/scss-lint-plugin/scss-lint-plugin";
    public static final String CONFIG = "";

    private static ScssLintRunner.ScssLintSettings createSettings(String targetFile) {
        return ScssLintRunner.buildSettings(PLUGIN_ROOT, targetFile, SCSS_EXE, CONFIG);
    }

    private static ScssLintRunner.ScssLintSettings createSettings() {
        return createSettings("");
    }

    @Test
    public void testMultiply() {
        String scssFile = "testData/one.scss";
//        String scssFile = PLUGIN_ROOT + "/testData/one.scss";
        ScssLintRunner.ScssLintSettings settings = createSettings(scssFile);
        try {
            LintResult result = ScssLintRunner.runLint(settings.cwd, settings.targetFile, SCSS_EXE, CONFIG);
//            System.out.println(result.lint.file.name);
//            System.out.println(result.lint.file.issues.size());
            assertEquals("file name should match", scssFile, result.lint.file.name);
            assertEquals("should have 1 issue", 1, result.lint.file.issues.size());
//            assertEquals("should have 1 issue", "1", result.lint.file.issues.get(0).reason);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMultiply2() {
        List<File> fromPath = PathEnvironmentVariableUtil.findAllExeFilesInPath(ScssLintFinder.SCSS_LINT_BASE_NAME);
        System.out.println(fromPath);
    }

    @Test
    public void testVersion() {
        ScssLintRunner.ScssLintSettings settings = createSettings();
        try {
            String version = ScssLintRunner.runVersion(settings);
            assertEquals("version should be", "scss-lint 0.27.0", version);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

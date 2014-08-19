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

    public static final String NODE_INTERPRETER = "/usr/local/bin/node";
    public static final String SCSS_LINT_BIN = "scss-lint";
    public static final String PLUGIN_ROOT = "/Users/idok/Projects/scss-lint-plugin/scss-lint-plugin";

    private static ScssLintRunner.ScssLintSettings createSettings(String targetFile) {
        return ScssLintRunner.buildSettings(PLUGIN_ROOT, targetFile, NODE_INTERPRETER, SCSS_LINT_BIN);
    }

    private static ScssLintRunner.ScssLintSettings createSettings() {
        return createSettings("");
    }

    @Test
    public void testMultiply() {
        ScssLintRunner.ScssLintSettings settings = createSettings(PLUGIN_ROOT + "/testData/one.scss");
        try {
            LintResult result = ScssLintRunner.runLint(settings.cwd, settings.targetFile, SCSS_LINT_BIN, null);
            System.out.println(result.lint.file.name);
            System.out.println(result.lint.file.issues.size());
            assertEquals("10 x 5 must be 50", PLUGIN_ROOT + "/testData/one.scss", result.lint.file.name);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testMultiply2() {
        List<File> fromPath = PathEnvironmentVariableUtil.findAllExeFilesInPath(ScssLintFinder.SCSS_LINT_BASE_NAME);
        System.out.println(fromPath);
    }

//    public static void main(String[] args) throws ExecutionException {
//        ESLintSettings settings = new ESLintSettings();
//        settings.node = "node";
//        settings.config = "";
//        settings.eslintExecutablePath = "/usr/local/bin/eslint";
//        settings.targetFile = "/Users/idok/Projects/eslint-plugin/testData/eq.js";
//        lint(settings);
//    }

    @Test
    public void testVersion() {
        ScssLintRunner.ScssLintSettings settings = createSettings();
        ProcessOutput out;
        try {
            out = ScssLintRunner.version(settings);
            System.out.println(settings);
            System.out.println(out.getStdout());
            assertEquals("exit code should be 0", 0, out.getExitCode());
            assertEquals("version should be", "v0.7.4", out.getStdout().trim());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

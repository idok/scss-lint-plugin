package com.scsslint.utils;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.scss.utils.ScssLintFinder;
import com.scss.utils.ScssLintRunner;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.scsslint.utils.Settings.PLUGIN_ROOT;
import static com.scsslint.utils.Settings.SCSS_EXE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ScssLintRunnerTest {
    public static final String CONFIG = "";

    private static ScssLintRunner.ScssLintSettings createSettings(String targetFile) {
        return ScssLintRunner.buildSettings(PLUGIN_ROOT, targetFile, SCSS_EXE, CONFIG);
    }

    private static ScssLintRunner.ScssLintSettings createSettings() {
        return createSettings("");
    }

//    @Test
//    public void testMultiply() {
//        String scssFile = "testData/one.scss";
////        String scssFile = PLUGIN_ROOT + "/testData/one.scss";
//        ScssLintRunner.ScssLintSettings settings = createSettings(scssFile);
//        LintResult result = ScssLintRunner.runLint(settings.cwd, settings.targetFile, SCSS_EXE, CONFIG);
////            System.out.println(result.lint.file.name);
////            System.out.println(result.lint.file.issues.size());
////            assertEquals("file name should match", scssFile, result.lint.get(scssFile));
//        assertNotNull(result.lint);
//        assertEquals("should have 1 issue", 1, result.lint.get(scssFile).size());
//    }

    @Test
    public void testFindAllExeFilesInPath() {
        List<File> fromPath = PathEnvironmentVariableUtil.findAllExeFilesInPath(ScssLintFinder.SCSS_LINT_BASE_NAME);
//        System.out.println(fromPath);
        assertEquals("should have size", 2, fromPath.size());
        assertArrayEquals("should have 2 paths", Arrays.asList("/usr/local/bin/scss-lint", "/usr/local/bin/scss-lint").toArray(), fromPath.stream().map(File::toString).toArray());
    }

//    @Test
//    public void testVersion() {
//        ScssLintRunner.ScssLintSettings settings = createSettings();
//        try {
//            String version = ScssLintRunner.runVersion(settings);
//            assertEquals("version should be", "scss-lint 0.27.0", version);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//    }
}

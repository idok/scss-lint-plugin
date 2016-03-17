package com.scsslint.utils;

import com.scss.utils.ConfigFinder;
import com.scss.utils.ScssLintRunner;
import org.junit.Test;

public class ConfigFinderTest {

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
        ConfigFinder.INSTANCE.readConfig("~/Projects/packages-plugin-test", "/Users/idok/Projects/packages-plugin-test/scss/.scss-lint.yml");
    }
}

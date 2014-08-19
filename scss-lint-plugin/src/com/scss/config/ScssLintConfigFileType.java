package com.scss.config;

import com.intellij.openapi.fileTypes.LanguageFileType;
import icons.ScssLintIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLLanguage;

import javax.swing.*;

public class ScssLintConfigFileType extends LanguageFileType {
    public static final ScssLintConfigFileType INSTANCE = new ScssLintConfigFileType();
    public static final String SCSS_LINT_YML = "scss-lint.yml";
    public static final String SCSS_LINT_YML_NAME = "." + SCSS_LINT_YML;
    public static final String SCSS_LINT_YAML_NAME = ".scss-lint.yaml";

    public static boolean isScssConfigFile(String name) {
        return name.equals(SCSS_LINT_YML_NAME) || name.equals(SCSS_LINT_YAML_NAME);
    }

    private ScssLintConfigFileType() {
        super(YAMLLanguage.INSTANCE);
    }

    @NotNull
    public String getName() {
        return "SCSS Lint";
    }

    @NotNull
    public String getDescription() {
        return "SCSS Lint configuration file";
    }

    @NotNull
    public String getDefaultExtension() {
        return SCSS_LINT_YML;
    }

    @NotNull
    public Icon getIcon() {
        return ScssLintIcons.ESLint;
    }
}
package com.scss.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;
import com.scss.utils.scssLint.Lint;
import com.scss.utils.scssLint.LintResult;
import com.wix.nodejs.NodeRunner;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public final class ScssLintRunner {
    private ScssLintRunner() {
    }

    private static final Logger LOG = Logger.getInstance(ScssLintRunner.class);

    private static final int TIME_OUT = (int) TimeUnit.SECONDS.toMillis(120L);
    /**
     * One or more files specified were not found
     */
    private static final int FILES_NOT_FOUND = 66;

    public static class ScssLintSettings {
        public ScssLintSettings() {
        }

        public ScssLintSettings(String config, String cwd, String targetFile, String scssLintExe) {
            this.config = config;
            this.cwd = cwd;
            this.targetFile = targetFile;
            this.scssLintExe = scssLintExe;
        }

        public String config;
        public String cwd;
        public String targetFile;
        public String scssLintExe;
    }

    public static ScssLintSettings buildSettings(@NotNull String cwd, @NotNull String path, @NotNull String scssLintExe, @Nullable String config) {
        ScssLintSettings settings = new ScssLintSettings();
        settings.cwd = cwd;
        settings.scssLintExe = scssLintExe;
        settings.config = config;
        settings.targetFile = path;
        return settings;
    }

    public static LintResult runLint(@NotNull String cwd, @NotNull String file, @NotNull String scssLintExe, @Nullable String config) throws ExecutionException {
        LintResult result = new LintResult();
        try {
            ProcessOutput out = lint(cwd, file, scssLintExe, config);
//        if (out.getExitCode() == 0) {
//        } else {
            result.errorOutput = out.getStderr();
            try {
                if (out.getExitCode() != FILES_NOT_FOUND) {
                    result.lint = Lint.read(out.getStdout());
                }
            } catch (Exception e) {
                result.errorOutput = out.getStdout();
            }
//        }
        } catch (Exception e) {
            e.printStackTrace();
            result.errorOutput = e.toString();
        }
        return result;
    }

    @NotNull
    public static ProcessOutput lint(@NotNull String cwd, @NotNull String file, @NotNull String scssLintExe, @Nullable String config) throws ExecutionException {
        //scss-lint one.scss -f XML
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setWorkDirectory(cwd);
//        if (SystemInfo.isWindows) {
//            commandLine.setExePath(settings.eslintExecutablePath);
//        } else {
//            commandLine.setExePath(settings.node);
//            commandLine.addParameter(settings.eslintExecutablePath);
//        }
        commandLine.setExePath(scssLintExe);
//        GeneralCommandLine commandLine = createCommandLine(buildSettings(cwd, file, scssLintExe, config));
        commandLine.addParameter(file);
        commandLine.addParameter("-f");
        commandLine.addParameter("XML");
        if (StringUtils.isNotEmpty(config)) {
            commandLine.addParameter("-c");
            commandLine.addParameter(config);
        }
        return NodeRunner.execute(commandLine, TIME_OUT);
    }

    @NotNull
    private static ProcessOutput version(@NotNull ScssLintSettings settings) throws ExecutionException {
        GeneralCommandLine commandLine = createCommandLine(settings);
        commandLine.addParameter("-v");
        return NodeRunner.execute(commandLine, TIME_OUT);
    }

    @NotNull
    public static String runVersion(@NotNull ScssLintSettings settings) throws ExecutionException {
        if (!new File(settings.scssLintExe).exists()) {
            LOG.warn("Calling version with invalid scssLintExe exe " + settings.scssLintExe);
            return "";
        }
        ProcessOutput out = version(settings);
        if (out.getExitCode() == 0) {
            return out.getStdout().trim();
        }
        return "";
    }

    @NotNull
    private static GeneralCommandLine createCommandLine(@NotNull ScssLintSettings settings) {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setWorkDirectory(settings.cwd);
        commandLine.setExePath(settings.scssLintExe);
        return commandLine;
    }
}
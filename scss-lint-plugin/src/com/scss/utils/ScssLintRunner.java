package com.scss.utils;

import com.google.common.base.Charsets;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.scss.utils.scssLint.Lint;
import com.scss.utils.scssLint.LintResult;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public final class ScssLintRunner {
    private ScssLintRunner() {
    }

    private static final Logger LOG = Logger.getInstance(ScssLintRunner.class);

    private static final int TIME_OUT = (int) TimeUnit.SECONDS.toMillis(120L);

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
                result.lint = Lint.read(out.getStdout());
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
        return execute(commandLine, TIME_OUT);
    }

    @NotNull
    public static ProcessOutput version(@NotNull ScssLintSettings settings) throws ExecutionException {
        GeneralCommandLine commandLine = createCommandLine(settings);
        commandLine.addParameter("-v");
        return execute(commandLine, TIME_OUT);
    }

    @NotNull
    private static GeneralCommandLine createCommandLine(@NotNull ScssLintSettings settings) {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setWorkDirectory(settings.cwd);
        commandLine.setExePath(settings.scssLintExe);
        return commandLine;
    }

    @NotNull
    private static ProcessOutput execute(@NotNull GeneralCommandLine commandLine, int timeoutInMilliseconds) throws ExecutionException {
        LOG.info("Running scss-lint command: " + commandLine.getCommandLineString());
        Process process = commandLine.createProcess();
        OSProcessHandler processHandler = new ColoredProcessHandler(process, commandLine.getCommandLineString(), Charsets.UTF_8);
        final ProcessOutput output = new ProcessOutput();
        processHandler.addProcessListener(new ProcessAdapter() {
            public void onTextAvailable(ProcessEvent event, Key outputType) {
                if (outputType.equals(ProcessOutputTypes.STDERR)) {
                    output.appendStderr(event.getText());
                } else if (!outputType.equals(ProcessOutputTypes.SYSTEM)) {
                    output.appendStdout(event.getText());
                }
            }
        });
        processHandler.startNotify();
        if (processHandler.waitFor(timeoutInMilliseconds)) {
            output.setExitCode(process.exitValue());
        } else {
            processHandler.destroyProcess();
            output.setTimeout();
        }
        if (output.isTimeout()) {
            throw new ExecutionException("Command '" + commandLine.getCommandLineString() + "' is timed out.");
        }
        return output;
    }
}
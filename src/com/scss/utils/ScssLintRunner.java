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
//        public String node;
//        public String eslintExecutablePath;
//        public String rules;
        public String config;
        public String cwd;
        public String targetFile;
    }

    public static ScssLintSettings buildSettings(@NotNull String cwd, @NotNull String path, @NotNull String nodeInterpreter, @NotNull String eslintBin, @Nullable String eslintrc, @Nullable String rulesdir) {
        ScssLintSettings settings = new ScssLintSettings();
        settings.cwd = cwd;
//        settings.eslintExecutablePath = eslintBin;
//        settings.node = nodeInterpreter;
//        settings.rules = rulesdir;
        settings.config = eslintrc;
        settings.targetFile = path;
        return settings;
    }

    public static LintResult runLint(@NotNull String cwd, @NotNull String file, @Nullable String config) throws ExecutionException {
        ProcessOutput out = lint(cwd, file, config);
        LintResult result = new LintResult();
//        if (out.getExitCode() == 0) {
//        } else {
            result.lint = Lint.read(out.getStdout());
            result.errorOutput = out.getStderr();
//        }
        return result;
    }

    @NotNull
    public static ProcessOutput lint(@NotNull String cwd, @NotNull String file, @Nullable String config) throws ExecutionException {
        //scss-lint one.scss -f XML
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setWorkDirectory(cwd);
//        if (SystemInfo.isWindows) {
//            commandLine.setExePath(settings.eslintExecutablePath);
//        } else {
//            commandLine.setExePath(settings.node);
//            commandLine.addParameter(settings.eslintExecutablePath);
//        }
        commandLine.setExePath(ScssLintFinder.SCSS_LINT_BASE_NAME);
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
//        if (SystemInfo.isWindows) {
//            commandLine.setExePath(settings.eslintExecutablePath);
//        } else {
//            commandLine.setExePath(settings.node);
//            commandLine.addParameter(settings.eslintExecutablePath);
//        }
        commandLine.setExePath(ScssLintFinder.SCSS_LINT_BASE_NAME);
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
package com.wix.nodejs;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.util.SystemInfo;

public class CLI {
    public static final String JSON = "json";
    public static final String FORMAT = "-f";
    public static final String NO_COLOR = "--no-color";
    public static final String VERSION = "--version";
    public static final String V = "-v";
    public static final String PARAM = "--";
    public static final String ALIAS = "-";

    private final GeneralCommandLine commandLine = new GeneralCommandLine();

    public CLI(String cwd, String node, String exe) {
        if (SystemInfo.isWindows) {
            commandLine.setExePath(exe);
        } else {
            commandLine.setExePath(node);
            commandLine.addParameter(exe);
        }
        commandLine.setWorkDirectory(cwd);
    }

    public CLI noColor() {
        return param(NO_COLOR);
    }

    public CLI param(String value) {
        commandLine.addParameter(value);
        return this;
    }

    public CLI param(String key, String value) {
        commandLine.addParameter(key);
        commandLine.addParameter(value);
        return this;
    }

    public CLI json() {
        return param(FORMAT, JSON);
    }

    public GeneralCommandLine cmd() {
        return commandLine;
    }
}

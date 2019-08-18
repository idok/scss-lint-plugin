package com.scss.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "ScssLintProjectComponent",
        storages = @Storage("scssLintPlugin.xml")
)
public class Settings implements PersistentStateComponent<Settings> {
    public String scssLintConfigFile = "";
    public String scssLintExecutable = "";
    public boolean treatAllIssuesAsWarnings;
    public boolean pluginEnabled;
    public boolean dismissConfigurationHints;

    public static Settings getInstance(Project project) {
        return ServiceManager.getService(project, Settings.class);
    }

    @Nullable
    @Override
    public Settings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull Settings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getVersion() {
        return scssLintExecutable + scssLintConfigFile + treatAllIssuesAsWarnings;
    }
}

package com.scss;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.scss.settings.Settings;
import com.wix.utils.FileUtils;
import com.wix.utils.FileUtils.ValidationStatus;
import org.jetbrains.annotations.NotNull;

public class ScssLintProjectComponent implements ProjectComponent {
    public static final String FIX_CONFIG_HREF = "\n<a href=\"#\">Fix Configuration</a>";
    protected final Project project;
    protected Settings settings;
    protected boolean settingValidStatus;
    protected String settingValidVersion;
    protected String settingVersionLastShowNotification;

    private static final Logger LOG = Logger.getInstance(ScssLintBundle.LOG_ID);

    public String scssLintConfigFile;
    public String scssLintExecutable;
    public boolean treatAsWarnings;
    public boolean pluginEnabled;
    public boolean dismissConfigurationHints;

    public static final String PLUGIN_NAME = "SCSS Lint";

    public ScssLintProjectComponent(Project project) {
        this.project = project;
        settings = Settings.getInstance(project);
    }

    @Override
    public void projectOpened() {
        if (isEnabled()) {
            isSettingsValid();
        }
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
        if (isEnabled()) {
            isSettingsValid();
        }
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return ScssLintProjectComponent.class.getName();
    }

    public boolean isEnabled() {
        return Settings.getInstance(project).pluginEnabled;
    }

    public boolean isDismissConfigurationHints() {
        return Settings.getInstance(project).dismissConfigurationHints;
    }

    public boolean isSettingsValid() {
        if (!settings.getVersion().equals(settingValidVersion)) {
            validateSettings();
            settingValidVersion = settings.getVersion();
        }
        return settingValidStatus;
    }

    public void validateSettings() {
        settingValidStatus = isValid();
        if (!settingValidStatus) {
            return;
        }

//        if (StringUtil.isNotEmpty(settings.scssLintExecutable)) {
//            File file = new File(project.getBasePath(), settings.scssLintExecutable);
//            if (!file.exists()) {
//                showErrorConfigNotification(ESLintBundle.message("eslint.rules.dir.does.not.exist", file.toString()));
//                LOG.debug("Rules directory not found");
//                settingValidStatus = false;
//                return false;
//            }
//        }
        scssLintExecutable = settings.scssLintExecutable;
        scssLintConfigFile = settings.scssLintConfigFile;
        treatAsWarnings = settings.treatAllIssuesAsWarnings;
        pluginEnabled = settings.pluginEnabled;
        dismissConfigurationHints = settings.dismissConfigurationHints;
    }

    public boolean isValid() {
        // do not validate if disabled
        if (!settings.pluginEnabled) {
            return true;
        }
//        boolean status = validateField("Node Interpreter", settings.nodeInterpreter, true, false, true);
//        if (!status) {
//            return false;
//        }
//        status = validateField("Rules", settings.rulesPath, false, true, false);
//        if (!status) {
//            return false;
//        }
        boolean status = validateField("SCSS Lint bin", settings.scssLintExecutable, false, false, true);
        if (!status) {
            return false;
        }
        return true;
    }

    private boolean validateField(String fieldName, String value, boolean shouldBeAbsolute, boolean allowEmpty, boolean isFile) {
        ValidationStatus r = FileUtils.validateProjectPath(shouldBeAbsolute ? null : project, value, allowEmpty, isFile);
        if (r == ValidationStatus.IS_EMPTY && !allowEmpty) {
            String msg = ScssLintBundle.message("scss.path.is.empty", fieldName);
            validationFailed(msg);
            return false;
        }
        if (isFile) {
            if (r == ValidationStatus.NOT_A_FILE) {
                String msg = ScssLintBundle.message("scss.file.is.not.a.file", fieldName, value);
                validationFailed(msg);
                return false;
            }
        } else {
            if (r == ValidationStatus.NOT_A_DIRECTORY) {
                String msg = ScssLintBundle.message("scss.directory.is.not.a.dir", fieldName, value);
                validationFailed(msg);
                return false;
            }
        }
        if (r == ValidationStatus.DOES_NOT_EXIST) {
            String msg = ScssLintBundle.message("scss.file.does.not.exist", fieldName, value);
            validationFailed(msg);
            return false;
        }
        return true;
    }

    private void validationFailed(String msg) {
        NotificationListener notificationListener = (notification, event) -> ScssLintInspection.showSettings(project);
        String errorMessage = msg + FIX_CONFIG_HREF;
        showInfoNotification(errorMessage, NotificationType.WARNING, notificationListener);
        LOG.debug(msg);
        settingValidStatus = false;
    }

    protected void showErrorConfigNotification(String content) {
        if (!settings.getVersion().equals(settingVersionLastShowNotification)) {
            settingVersionLastShowNotification = settings.getVersion();
            showInfoNotification(content, NotificationType.WARNING);
        }
    }

    public void showInfoNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    public void showInfoNotification(String content, NotificationType type, NotificationListener notificationListener) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type, notificationListener);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    public static void showNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type);
        Notifications.Bus.notify(errorNotification);
    }
}

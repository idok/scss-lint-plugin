package com.scss.settings;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.ex.SingleConfigurableEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.ui.SwingHelper;
import com.intellij.util.ui.UIUtil;
import com.scss.ScssLintProjectComponent;
import com.scss.utils.ScssLintFinder;
import com.scss.utils.ScssLintRunner;
import com.wix.settings.ValidationUtils;
import com.wix.settings.Validator;
import com.wix.ui.PackagesNotificationPanel;
import com.wix.utils.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScssLintSettingsPage implements Configurable {
    private static final String FIX_IT = "Fix it";
    private static final String HOW_TO_USE_SCSS_LINT = "How to Use SCSS Lint";
    private static final String HOW_TO_USE_LINK = "https://github.com/idok/scss-lint-plugin";
    private final Project project;

    private JCheckBox pluginEnabledCheckbox;
    private JPanel panel;
    private JPanel errorPanel;
    private TextFieldWithHistoryWithBrowseButton scssLintConfigFile;
    private JRadioButton searchForConfigInRadioButton;
    private JRadioButton useSpecificConfigRadioButton;
    private HyperlinkLabel usageLink;
    private JLabel ScssLintConfigFilePathLabel;
    private JCheckBox treatAllIssuesCheckBox;
    private JLabel versionLabel;
    private JLabel scssLintExeLabel;
    private TextFieldWithHistoryWithBrowseButton scssLintExeField;
    private JCheckBox dismissConfigurationHints;
    private final PackagesNotificationPanel packagesNotificationPanel;

    public ScssLintSettingsPage(@NotNull final Project project) {
        this.project = project;
        configESLintBinField();
        configScssLintConfigField();
        this.packagesNotificationPanel = new PackagesNotificationPanel(project);
        errorPanel.add(this.packagesNotificationPanel.getComponent(), BorderLayout.CENTER);
    }

    private void addListeners() {
        useSpecificConfigRadioButton.addItemListener(e -> scssLintConfigFile.setEnabled(e.getStateChange() == ItemEvent.SELECTED));
        pluginEnabledCheckbox.addItemListener(e -> {
            boolean enabled = e.getStateChange() == ItemEvent.SELECTED;
            setEnabledState(enabled);
        });
        DocumentAdapter docAdp = new DocumentAdapter() {
            protected void textChanged(@NotNull DocumentEvent e) {
                updateLaterInEDT();
            }
        };
        scssLintExeField.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
        scssLintConfigFile.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
    }

    private void updateLaterInEDT() {
        UIUtil.invokeLaterIfNeeded(ScssLintSettingsPage.this::update);
    }

    private void update() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        validate();
    }

    private void setEnabledState(boolean enabled) {
        searchForConfigInRadioButton.setEnabled(enabled);
        useSpecificConfigRadioButton.setEnabled(enabled);
        scssLintConfigFile.setEnabled(enabled && useSpecificConfigRadioButton.isSelected());
        scssLintExeField.setEnabled(enabled);
        ScssLintConfigFilePathLabel.setEnabled(enabled);
        scssLintExeLabel.setEnabled(enabled);
        treatAllIssuesCheckBox.setEnabled(enabled);
    }

    private void validate() {
        Validator validator = new Validator();
        if (!ValidationUtils.validatePath(project, scssLintExeField.getChildComponent().getText(), false)) {
            validator.add(scssLintExeField.getChildComponent().getTextEditor(), "Path to Scss Lint exe is invalid {{LINK}}", FIX_IT);
        }
        if (!ValidationUtils.validatePath(project, scssLintConfigFile.getChildComponent().getText(), true)) {
            validator.add(scssLintConfigFile.getChildComponent().getTextEditor(), "Path to Scss Lint config is invalid {{LINK}}", FIX_IT); //Please correct path to
        }
        if (validator.hasErrors()) {
            versionLabel.setText("n.a.");
        } else {
            updateVersion();
        }
        packagesNotificationPanel.processErrors(validator);
    }

    private ScssLintRunner.ScssLintSettings settings;

    private void updateVersion() {
        String scssExe = scssLintExeField.getChildComponent().getText();
        if (settings != null &&
                settings.scssLintExe.equals(scssExe) &&
                settings.cwd.equals(project.getBasePath())) {
            return;
        }
        if (StringUtils.isEmpty(scssExe)) {
            return;
        }
        getVersion(scssExe, project.getBasePath());
    }

    private void getVersion(String scssExe, String cwd) {
        if (StringUtils.isEmpty(scssExe)) {
            return;
        }
        settings = new ScssLintRunner.ScssLintSettings();
        settings.scssLintExe = scssExe;
        settings.cwd = cwd;
        try {
            versionLabel.setText(ScssLintRunner.runVersion(settings));
        } catch (Exception e) {
            versionLabel.setText("error");
            e.printStackTrace();
        }
    }

    private void configESLintBinField() {
        TextFieldWithHistory textFieldWithHistory = scssLintExeField.getChildComponent();
        textFieldWithHistory.setHistorySize(-1);
        textFieldWithHistory.setMinimumAndPreferredWidth(0);

        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, () -> {
//                File projectRoot = new File(project.getBaseDir().getPath());
            List<File> newFiles = ScssLintFinder.findAllScssLintExe(); //searchForESLintBin(projectRoot);
            return FileUtils.toAbsolutePath(newFiles);
        });

        SwingHelper.installFileCompletionAndBrowseDialog(project, scssLintExeField, "Select SCSS Lint Exe", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    private void configScssLintConfigField() {
        TextFieldWithHistory textFieldWithHistory = scssLintConfigFile.getChildComponent();
        textFieldWithHistory.setHistorySize(-1);
        textFieldWithHistory.setMinimumAndPreferredWidth(0);

        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, () -> {
            String f = project.getBasePath();
            if (f != null) {
                File projectRoot = new File(f);
                return ScssLintFinder.searchForLintConfigFiles(projectRoot);
            }
            return new ArrayList<>();
        });

        SwingHelper.installFileCompletionAndBrowseDialog(project, scssLintConfigFile, "Select SCSS Lint Config", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "SCSS Lint";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        loadSettings();
        getVersion(scssLintExeField.getChildComponent().getText(), project.getBasePath());
        addListeners();
        return panel;
    }

    @Override
    public boolean isModified() {
        return pluginEnabledCheckbox.isSelected() != getSettings().pluginEnabled
                || dismissConfigurationHints.isSelected() != getSettings().dismissConfigurationHints
                || !scssLintExeField.getChildComponent().getText().equals(getSettings().scssLintExecutable)
                || treatAllIssuesCheckBox.isSelected() != getSettings().treatAllIssuesAsWarnings
                || !getLintConfigFile().equals(getSettings().scssLintConfigFile);
    }

    private String getLintConfigFile() {
        return useSpecificConfigRadioButton.isSelected() ? scssLintConfigFile.getChildComponent().getText() : "";
    }

    @Override
    public void apply() throws ConfigurationException {
        saveSettings();
        PsiManager.getInstance(project).dropResolveCaches();
    }

    private void saveSettings() {
        Settings settings = getSettings();
        settings.pluginEnabled = pluginEnabledCheckbox.isSelected();
        settings.scssLintExecutable = scssLintExeField.getChildComponent().getText();
        settings.scssLintConfigFile = getLintConfigFile();
        settings.treatAllIssuesAsWarnings = treatAllIssuesCheckBox.isSelected();
        settings.dismissConfigurationHints = dismissConfigurationHints.isSelected();
        project.getComponent(ScssLintProjectComponent.class).validateSettings();
        DaemonCodeAnalyzer.getInstance(project).restart();
    }

    private void loadSettings() {
        Settings settings = getSettings();
        pluginEnabledCheckbox.setSelected(settings.pluginEnabled);
        scssLintExeField.getChildComponent().setText(settings.scssLintExecutable);
        scssLintConfigFile.getChildComponent().setText(settings.scssLintConfigFile);

        boolean hasConfig = StringUtils.isNotEmpty(settings.scssLintConfigFile);
        searchForConfigInRadioButton.setSelected(!hasConfig);
        useSpecificConfigRadioButton.setSelected(hasConfig);
        scssLintConfigFile.setEnabled(hasConfig);
        treatAllIssuesCheckBox.setSelected(settings.treatAllIssuesAsWarnings);
        dismissConfigurationHints.setSelected(settings.dismissConfigurationHints);
        setEnabledState(settings.pluginEnabled);
    }

    @Override
    public void reset() {
        loadSettings();
    }

    @Override
    public void disposeUIResources() {
    }

    protected Settings getSettings() {
        return Settings.getInstance(project);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        usageLink = SwingHelper.createWebHyperlink(HOW_TO_USE_SCSS_LINT, HOW_TO_USE_LINK);
    }

    public void showSettings() {
        String dimensionKey = ShowSettingsUtilImpl.createDimensionKey(this);
        SingleConfigurableEditor singleConfigurableEditor = new SingleConfigurableEditor(project, this, dimensionKey, false);
        singleConfigurableEditor.show();
    }
}

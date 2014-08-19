package com.scss.settings;

import com.scss.ScssLintProjectComponent;
import com.scss.utils.ScssLintFinder;
import com.scss.utils.ScssLintRunner;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.Function;
import com.intellij.util.NotNullProducer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.UIUtil;
import com.intellij.webcore.packaging.PackagesNotificationPanel;
import com.intellij.webcore.ui.SwingHelper;
import com.wix.utils.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

//path to JavaScript: JSBundle.message("settings.javascript.root.configurable.name", new Object[0])
public class ScssLintSettingsPage implements Configurable {
    public static final String FIX_IT = "Fix it";
    public static final String HOW_TO_USE_SCSSLINT = "How to Use SCSS Lint";
    public static final String HOW_TO_USE_LINK = "https://github.com/idok/scss-lint-plugin";
    protected Project project;

    private JCheckBox pluginEnabledCheckbox;
    //    private JTextField rulesPathField;
    private JPanel panel;
    private JPanel errorPanel;
    //    private TextFieldWithHistoryWithBrowseButton eslintBinField2;
//    private TextFieldWithHistoryWithBrowseButton nodeInterpreterField;
    private TextFieldWithHistoryWithBrowseButton scssLintConfigFile;
    private JRadioButton searchForConfigInRadioButton;
    private JRadioButton useProjectConfigRadioButton;
    private HyperlinkLabel usageLink;
    private JLabel ScssLintConfigFilePathLabel;
    //    private JLabel rulesDirectoryLabel;
//    private JLabel pathToEslintBinLabel;
//    private JLabel nodeInterpreterLabel;
    private JCheckBox treatAllIssuesCheckBox;
    private JLabel versionLabel;
    private JLabel scssLintExeLabel;
    private TextFieldWithHistoryWithBrowseButton scssLintExeField;
    private final PackagesNotificationPanel packagesNotificationPanel;

    public ScssLintSettingsPage(@NotNull final Project project) {
        this.project = project;
        configESLintBinField();
        configScssLintConfigField();
//        configNodeField();
//        searchForConfigInRadioButton.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent e) {
//                scssLintConfigFile.setEnabled(e.getStateChange() == ItemEvent.DESELECTED);
//                System.out.println("searchForConfigInRadioButton: " + (e.getStateChange() == ItemEvent.SELECTED ? "checked" : "unchecked"));
//            }
//        });
        useProjectConfigRadioButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                scssLintConfigFile.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
//                System.out.println("useProjectConfigRadioButton: " + (e.getStateChange() == ItemEvent.SELECTED ? "checked" : "unchecked"));
            }
        });
        pluginEnabledCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean enabled = e.getStateChange() == ItemEvent.SELECTED;
                setEnabledState(enabled);
            }
        });

        this.packagesNotificationPanel = new PackagesNotificationPanel(project);
//        GridConstraints gridConstraints = new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
//                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
//                null, new Dimension(250, 150), null);
        errorPanel.add(this.packagesNotificationPanel.getComponent(), BorderLayout.CENTER);

        DocumentAdapter docAdp = new DocumentAdapter() {
            protected void textChanged(DocumentEvent e) {
                updateLaterInEDT();
            }
        };
        scssLintExeField.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
        scssLintConfigFile.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
//        nodeInterpreterField.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
//        rulesPathField.getDocument().addDocumentListener(docAdp);
        getVersion();
    }

    private void updateLaterInEDT() {
        UIUtil.invokeLaterIfNeeded(new Runnable() {
            public void run() {
                ScssLintSettingsPage.this.update();
            }
        });
    }

    private void update() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        validate();
    }

    private void setEnabledState(boolean enabled) {
        scssLintConfigFile.setEnabled(enabled);
//        rulesPathField.setEnabled(enabled);
        searchForConfigInRadioButton.setEnabled(enabled);
        useProjectConfigRadioButton.setEnabled(enabled);
        scssLintExeField.setEnabled(enabled);
//        nodeInterpreterField.setEnabled(enabled);
        ScssLintConfigFilePathLabel.setEnabled(enabled);
//        rulesDirectoryLabel.setEnabled(enabled);
        scssLintExeLabel.setEnabled(enabled);
//        nodeInterpreterLabel.setEnabled(enabled);
        treatAllIssuesCheckBox.setEnabled(enabled);
    }

    private void validate() {
        List<ScssLintValidationInfo> errors = new ArrayList<ScssLintValidationInfo>();
        if (!validatePath(scssLintExeField.getChildComponent().getText(), false)) {
            ScssLintValidationInfo error = new ScssLintValidationInfo(scssLintExeField.getChildComponent().getTextEditor(), "Path to scss lint exe is invalid {{LINK}}", FIX_IT);
            errors.add(error);
        }
        if (!validatePath(scssLintConfigFile.getChildComponent().getText(), true)) {
            ScssLintValidationInfo error = new ScssLintValidationInfo(scssLintConfigFile.getChildComponent().getTextEditor(), "Path to scss-lint config is invalid {{LINK}}", FIX_IT); //Please correct path to
            errors.add(error);
        }
//        if (!validatePath(nodeInterpreterField.getChildComponent().getText(), false)) {
//            ScssLintValidationInfo error = new ScssLintValidationInfo(nodeInterpreterField.getChildComponent().getTextEditor(), "Path to node interpreter is invalid {{LINK}}", FIX_IT);
//            errors.add(error);
//        }
//        if (!validateDirectory(rulesPathField.getText(), true)) {
//            ScssLintValidationInfo error = new ScssLintValidationInfo(rulesPathField, "Path to rules is invalid {{LINK}}", FIX_IT);
//            errors.add(error);
//        }
        if (errors.isEmpty()) {
            packagesNotificationPanel.removeAllLinkHandlers();
            packagesNotificationPanel.hide();
            getVersion();
        } else {
            showErrors(errors);
        }
    }

    private ScssLintRunner.ScssLintSettings settings;

    private void getVersion() {
        if (settings != null &&
//                settings.node.equals(nodeInterpreterField.getChildComponent().getText()) &&
                settings.scssLintExe.equals(scssLintExeField.getChildComponent().getText()) &&
                settings.cwd.equals(project.getBasePath())) {
            return;
        }
        if (StringUtils.isEmpty(scssLintExeField.getChildComponent().getText())) {
            return;
        }
        settings = new ScssLintRunner.ScssLintSettings();
//        settings.node = nodeInterpreterField.getChildComponent().getText();
        settings.scssLintExe = scssLintExeField.getChildComponent().getText();
        settings.cwd = project.getBasePath();
        try {
            ProcessOutput out = ScssLintRunner.version(settings);
            if (out.getExitCode() == 0) {
                versionLabel.setText(out.getStdout().trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validatePath(String path, boolean allowEmpty) {
        if (StringUtils.isEmpty(path)) {
            return allowEmpty;
        }
        File filePath = new File(path);
        if (filePath.isAbsolute()) {
            if (!filePath.exists() || !filePath.isFile()) {
                return false;
            }
        } else {
            VirtualFile child = project.getBaseDir().findFileByRelativePath(path);
            if (child == null || !child.exists() || child.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    private boolean validateDirectory(String path, boolean allowEmpty) {
        if (StringUtils.isEmpty(path)) {
            return allowEmpty;
        }
        File filePath = new File(path);
        if (filePath.isAbsolute()) {
            if (!filePath.exists() || !filePath.isDirectory()) {
                return false;
            }
        } else {
            VirtualFile child = project.getBaseDir().findFileByRelativePath(path);
            if (child == null || !child.exists() || !child.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    private void configESLintBinField() {
        TextFieldWithHistory textFieldWithHistory = scssLintExeField.getChildComponent();
        textFieldWithHistory.setHistorySize(-1);
        textFieldWithHistory.setMinimumAndPreferredWidth(0);

        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, new NotNullProducer<List<String>>() {
            @NotNull
            public List<String> produce() {
//                File projectRoot = new File(project.getBaseDir().getPath());
                List<File> newFiles = ScssLintFinder.findAllScssLintExe(); //searchForESLintBin(projectRoot);
                return FileUtils.toAbsolutePath(newFiles);
            }
        });

        SwingHelper.installFileCompletionAndBrowseDialog(project, scssLintExeField, "Select SCSS Lint exe", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    private void configScssLintConfigField() {
        TextFieldWithHistory textFieldWithHistory = scssLintConfigFile.getChildComponent();
        textFieldWithHistory.setHistorySize(-1);
        textFieldWithHistory.setMinimumAndPreferredWidth(0);

        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, new NotNullProducer<List<String>>() {
            @NotNull
            public List<String> produce() {
                File projectRoot = new File(project.getBaseDir().getPath());
                return ScssLintFinder.searchForLintConfigFiles(projectRoot);
            }
        });

        SwingHelper.installFileCompletionAndBrowseDialog(project, scssLintConfigFile, "Select SCSS Lint config", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

//    private void configNodeField() {
//        TextFieldWithHistory textFieldWithHistory = nodeInterpreterField.getChildComponent();
//        textFieldWithHistory.setHistorySize(-1);
//        textFieldWithHistory.setMinimumAndPreferredWidth(0);
//
//        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, new NotNullProducer<List<String>>() {
//            @NotNull
//            public List<String> produce() {
//                List<File> newFiles = NodeDetectionUtil.listAllPossibleNodeInterpreters();
//                return FileUtils.toAbsolutePath(newFiles);
//            }
//        });
//
//        SwingHelper.installFileCompletionAndBrowseDialog(project, nodeInterpreterField, "Select Node interpreter", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
//    }

    @Nls
    @Override
    public String getDisplayName() {
        return "SCSS Lint Plugin";
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
        return panel;
    }

    @Override
    public boolean isModified() {
        return pluginEnabledCheckbox.isSelected() != getSettings().pluginEnabled
                || !scssLintExeField.getChildComponent().getText().equals(getSettings().scssLintExecutable)
//                || !nodeInterpreterField.getChildComponent().getText().equals(getSettings().nodeInterpreter)
                || treatAllIssuesCheckBox.isSelected() != getSettings().treatAllIssuesAsWarnings
//                || !rulesPathField.getText().equals(getSettings().rulesPath)
                || !getLintConfigFile().equals(getSettings().scssLintConfigFile);
    }

    private String getLintConfigFile() {
        return useProjectConfigRadioButton.isSelected() ? scssLintConfigFile.getChildComponent().getText() : "";
    }

    @Override
    public void apply() throws ConfigurationException {
        saveSettings();
        PsiManager.getInstance(project).dropResolveCaches();
    }

    protected void saveSettings() {
        Settings settings = getSettings();
        settings.pluginEnabled = pluginEnabledCheckbox.isSelected();
        settings.scssLintExecutable = scssLintExeField.getChildComponent().getText();
//        settings.nodeInterpreter = nodeInterpreterField.getChildComponent().getText();
        settings.scssLintConfigFile = getLintConfigFile();
//        settings.rulesPath = rulesPathField.getText();
        settings.treatAllIssuesAsWarnings = treatAllIssuesCheckBox.isSelected();
        project.getComponent(ScssLintProjectComponent.class).validateSettings();
        DaemonCodeAnalyzer.getInstance(project).restart();
    }

    protected void loadSettings() {
        Settings settings = getSettings();
        pluginEnabledCheckbox.setSelected(settings.pluginEnabled);
        scssLintExeField.getChildComponent().setText(settings.scssLintExecutable);
        scssLintConfigFile.getChildComponent().setText(settings.scssLintConfigFile);
//        nodeInterpreterField.getChildComponent().setText(settings.nodeInterpreter);
//        rulesPathField.setText(settings.rulesPath);
        useProjectConfigRadioButton.setSelected(StringUtils.isNotEmpty(settings.scssLintConfigFile));
        searchForConfigInRadioButton.setSelected(StringUtils.isEmpty(settings.scssLintConfigFile));
        scssLintConfigFile.setEnabled(useProjectConfigRadioButton.isSelected());
        treatAllIssuesCheckBox.setSelected(settings.treatAllIssuesAsWarnings);
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
        usageLink = SwingHelper.createWebHyperlink(HOW_TO_USE_SCSSLINT, HOW_TO_USE_LINK);
    }

    private void showErrors(@NotNull List<ScssLintValidationInfo> errors) {
        List<String> errorHtmlDescriptions = ContainerUtil.map(errors, new Function<ScssLintValidationInfo, String>() {
            public String fun(ScssLintValidationInfo info) {
                return info.getErrorHtmlDescription();
            }
        });
        String styleTag = UIUtil.getCssFontDeclaration(UIUtil.getLabelFont());
        String html = "<html>" + styleTag + "<body><div style='padding-left:4px;'>" + StringUtil.join(errorHtmlDescriptions, "<div style='padding-top:2px;'/>") + "</div></body></html>";

        for (ScssLintValidationInfo error : errors) {
            String linkText = error.getLinkText();
            final JTextComponent component = error.getTextComponent();
            if (linkText != null && component != null) {
                this.packagesNotificationPanel.addLinkHandler(linkText, new Runnable() {
                    public void run() {
                        component.requestFocus();
                    }
                });
            }
        }
        this.packagesNotificationPanel.showError(html, null, null);
    }
}

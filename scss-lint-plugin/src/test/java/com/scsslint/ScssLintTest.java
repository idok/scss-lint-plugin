//package com.scsslint;
//
//import com.intellij.openapi.project.Project;
//import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
//import com.scss.ScssLintExternalAnnotator;
//import com.scss.ScssLintInspection;
//import com.scss.settings.Settings;
////import org.jetbrains.plugins.scss.SCSSFileType;
//
//public class ScssLintTest extends LightPlatformCodeInsightFixtureTestCase {
//    @Override
//    protected String getTestDataPath() {
//        return TestUtils.getTestDataPath();
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//    }
//
//    @Override
//    protected boolean isWriteActionRequired() {
//        return false;
//    }
//
//    private void doTest(final String file) {
//        Project project = myFixture.getProject();
//        Settings settings = Settings.getInstance(project);
////        settings.scssLintExecutable = ScssLintRunnerTest.SCSS_LINT_BIN;
////        settings.scssLintConfigFile = getTestDataPath() + "/.eslintrc";
////        settings.nodeInterpreter = ScssLintRunnerTest.SCSS_EXE;
////        settings.rulesPath = "";
//        settings.pluginEnabled = true;
//        myFixture.configureByFile(file);
//        myFixture.enableInspections(new ScssLintInspection());
//        myFixture.checkHighlighting(true, false, true);
//    }
//
//    private void doTest() {
//        String name = getTestName(false).replaceAll("_", "-");
//        doTest("/inspections/" + name + '.' + ScssLintExternalAnnotator.SCSS);
//    }
//
//    public void testCapitalizationInSelector() {
//        doTest();
//    }
//
//    public void testEmptyRule() {
//        doTest();
//    }
//
//    public void testHexLength() {
//        doTest();
//    }
//}

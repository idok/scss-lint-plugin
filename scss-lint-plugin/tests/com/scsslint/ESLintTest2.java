//package com.eslint;
//
//import com.eslint.settings.Settings;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.vfs.LocalFileSystem;
//import com.intellij.openapi.vfs.VirtualFile;
//import com.intellij.testFramework.fixtures.*;
//import org.junit.After;
//
//public class ESLintTest2 extends LightPlatformCodeInsightFixtureTestCase {
//    @Override
//    protected String getTestDataPath() {
////        return TestUtils.getTestDataPath();
//        return "/Users/idok/Projects/eslint-plugin/testData";
//    }
//
//    //    @Override
////    protected String getBasePath() {
////        return FileUtil.toSystemDependentName("/move/");
////    }
//    //@Before
//    @Override
//    protected void setUp() throws Exception {
//        IdeaTestFixtureFactory factory = IdeaTestFixtureFactory.getFixtureFactory();
//        TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = factory.createFixtureBuilder("eslint");
//        final IdeaProjectTestFixture fixture = fixtureBuilder.getFixture();
//        myTempDirFixture = IdeaTestFixtureFactory.getFixtureFactory().createTempDirTestFixture();
//
//        myFixture = IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(fixture, myTempDirFixture);
//
//        myFixture.setUp();
//
//        myFixture.setTestDataPath(getTestDataPath());
//        myFixture.allowTreeAccessForAllFiles();
//
////        ApplicationManager.getApplication().runWriteAction(new Runnable() {
////            @Override
////            public void run() {
////                copyFile("/inspections/eq1.js", "eq1.js");
////                copyFile("/.eslintrc", ".eslintrc");
////            }
////        });
//
//        myModule = myFixture.getModule();
//
////        orgTestDialog = Messages.setTestDialog(new TestDialog() {
////            public int show(String message) {
////                shownMessage = message;
////                return dialogResult;
////            }
////        });
//    }
//
//    private void copyFile(String file, String name) {
//        VirtualFile myFile = LocalFileSystem.getInstance().findFileByPath(getTestDataPath() + file);
//        myTempDirFixture.copyFile(myFile, name);
//    }
//
//
//    @After
//    public void tearDown() throws Exception {
////        Messages.setTestDialog(orgTestDialog);
////        fixture.tearDown();
//        super.tearDown();
//        myTempDirFixture.tearDown();
//    }
//
////    IdeaProjectTestFixture fixture;
//    private TempDirTestFixture myTempDirFixture;
//
//    @Override
//    protected boolean isWriteActionRequired() {
//        return false;
//    }
//
//    protected void doTest2(final String file) {
//        Project project = myFixture.getProject();
//        Settings settings = Settings.getInstance(project);
//        settings.scssLintExecutable = "/usr/local/bin/eslint";
//        settings.scssLintConfigFile = "/Users/idok/Projects/eslint-plugin/testData/.eslintrc";
//        settings.nodeInterpreter = "/usr/local/bin/node";
//        settings.rulesPath = "";
//        settings.pluginEnabled = true;
////        myFixture.copyFileToProject(file);
////        myFixture.copyDirectoryToProject("node_modules", "node_modules");
//        //        myFixture.copyFileToProject("/.eslintrc");
//        //        myFixture.copyFileToProject(file);
////        myFixture.configureByFile(file);
////        myFixture.configureFromTempProjectFile(file);
////        myFixture.configureByText("eq.js", "function f() {}");
//        myFixture.configureByFiles(file);
//        myFixture.enableInspections(new ESLintInspection());
//
////        myFixture.testHighlighting(true, false, false, file);
////        System.out.println("$$$$ testHighlighting = " + r);
////        myFixture.checkHighlighting();
////        List<HighlightInfo> highlightInfos = myFixture.doHighlighting();
//        myFixture.checkResultByFile("/inspections/missing-strict-result.js");
//        myFixture.checkHighlighting(false, false, true);
////        myFixture.checkHighlighting();
////        System.out.println("$$$$ highlightInfos.size = " + highlightInfos.size());
////
////        if (highlightInfos.size() != expectedHighlightInfos.size()) {
////            Assert.assertEquals(highlightInfos.size(), expectedHighlightInfos.size());
////        }
////        for (int i = 0; i < highlightInfos.size(); i++) {
////            Assert.assertEquals(highlightInfos.get(i), expectedHighlightInfos.get(i));
////        }
//    }
//
//    protected void doTest(final String file) {
//        Project project = myFixture.getProject();
//        Settings settings = Settings.getInstance(project);
//        settings.scssLintExecutable = "/usr/local/bin/eslint";
//        settings.scssLintConfigFile = "/Users/idok/Projects/eslint-plugin/testData/.eslintrc";
//        settings.nodeInterpreter = "/usr/local/bin/node";
//        settings.rulesPath = "";
//        settings.pluginEnabled = true;
//        myFixture.configureByFile(file);
//        myFixture.enableInspections(new ESLintInspection());
//        myFixture.checkHighlighting(false, false, true);
//    }
//
////    public void testStepDefClassInNamedPackage() {
////        doTest("/inspections/eq1.js");
////    }
//
////    public void testUseStrict2() {
//////        List<HighlightInfo> highlightInfos = new ArrayList<HighlightInfo>();
//////        HighlightInfo highlightInfo = HighlightInfo.newHighlightInfo(HighlightInfoType.ERROR).description("").create();
//////        highlightInfos.add(highlightInfo);
//////        doTest("/inspections/eq2.js");
////    }
//
//    public void testeqeqeq() {
//        doTest("/inspections/eqeqeq.js");
//    }
//
//    public void testno_negated_in_lhs() {
//        doTest("/inspections/no-negated-in-lhs.js");
//    }
//
////    public void testUseStrict4() {
////        doTest("/inspections/missing-strict-result.js");
////    }
//
////    public void testUseStrict3() {
////        doTest("/inspections/missing-strict.js");
////    }
//
////    @Override
////    protected String getBasePath() {
////        return CucumberJavaTestUtil.RELATED_TEST_DATA_PATH + "inspections/stepDefClassInDefaultPackage";
////    }
//}

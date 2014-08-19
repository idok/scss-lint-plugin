//package com.unit;
//
//import com.eslint.utils.ESLintRunner;
//import com.intellij.execution.ExecutionException;
//import com.intellij.execution.process.ProcessOutput;
//import org.junit.Test;
//
//import static org.junit.Assert.assertEquals;
//
//public class ESLintUnit {
//    @Test
//    public void testMultiply() throws ExecutionException {
//        ESLintRunner.ESLintSettings settings = new ESLintRunner.ESLintSettings();
//        settings.node = "node";
//        settings.config = "";
//        settings.eslintExecutablePath = "/usr/local/bin/eslint";
//        settings.targetFile = "/Users/idok/Projects/eslint-plugin/testData/eq.js";
//        ProcessOutput out = ESLintRunner.lint(settings);
//        assertEquals("10 x 5 must be 50", 0, out.getExitCode());
//    }
//}

//package com.scss.config;
//
//import com.intellij.openapi.fileTypes.ExactFileNameMatcher;
//import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher;
//import com.intellij.openapi.fileTypes.FileTypeConsumer;
//import com.intellij.openapi.fileTypes.FileTypeFactory;
//import org.jetbrains.annotations.NotNull;
//
//public class ScssLintConfigFileTypeFactory extends FileTypeFactory {
//    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
//        consumer.consume(ScssLintConfigFileType.INSTANCE,
//                new ExactFileNameMatcher(ScssLintConfigFileType.SCSS_LINT_YML_NAME),
//                new ExactFileNameMatcher(ScssLintConfigFileType.SCSS_LINT_YAML_NAME));
////        consumer.consume(ScssLintConfigFileType.INSTANCE, new ExtensionFileNameMatcher(ScssLintConfigFileType.SCSS_LINT_YML));
//        //, new ExactFileNameMatcher("eslint.json")
//    }
//}
//package com.eslint.utils;
//
//import com.intellij.util.EnvironmentUtil;
//import org.apache.commons.lang.SystemUtils;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.FilenameFilter;
//import java.util.List;
//
//public class ESLintDetectionUtilTest {
//    @Test
//    public void testMultiply() {
//        List<File> list = ESLintFinder.listAllPossibleNodeInterpreters();
//        if (!list.isEmpty()) {
//            File file = list.get(0);
//            System.out.println(file);
//        }
//    }
//
//    @Test
//    public void search() {
//        File root = new File("/Users/idok/Projects/react-viewer");
//
////        File[] files = root.listFiles(new FileFilter() {
////            @Override
////            public boolean accept(File file) {
////                return false;
////            }
////        });
//
//        FilenameFilter nodeModulesFilter = new FileFilter("node_modules", true);
//        File[] files = root.listFiles(nodeModulesFilter);
//        if (files.length > 0) {
//            File eslint = searchForESLint(files[0]);
//            if (eslint != null) {
//
//            }
//        }
//
//
////        FileUtils.displayDirectoryContents()
//    }
//
//
//    @Test
//    public void printPaths() {
//        System.out.println(SystemUtils.getUserDir());
//        System.out.println(SystemUtils.getUserHome());
//        System.out.println("NODE_HOME=" + EnvironmentUtil.getValue("NODE_HOME"));
//        System.out.println("NVM_HOME=" + EnvironmentUtil.getValue("NVM_HOME"));
//        System.out.println("USERPROFILE=" + EnvironmentUtil.getValue("USERPROFILE"));
//    }
//
//    private static File searchForESLint(File nodeModules) {
//        FilenameFilter nodeModulesFilter = new FileFilter("eslint", true);
//        File[] files = nodeModules.listFiles(nodeModulesFilter);
//        if (files.length > 0) {
//            return files[0];
//        }
//        return null;
//    }
//
//    static class FileFilter implements FilenameFilter {
//        FileFilter(String name) {
//            this(name, false);
//        }
//
//        FileFilter(String name, boolean directory) {
//            this.name = name;
//            this.directory = directory;
//        }
//
//        String name;
//        boolean directory;
//
//        @Override
//        public boolean accept(File file, String name) {
//            return name.equals(this.name);
//        }
//    }
//}

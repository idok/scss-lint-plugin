package com.scsslint;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * @author ilyas
 */
public final class TestUtils2 {

    public static final String BASE_TEST_DATA_PATH = findTestDataPath();
    public static final String SDK_HOME_PATH = BASE_TEST_DATA_PATH + "/sdk";

    private TestUtils2() {
    }

    private static String findTestDataPath() {
        final File f = new File("/Users/idok/Projects/eslint-plugin/testData"); // launched from 'Dart-plugin' project
        if (f.isDirectory()) return FileUtil.toSystemIndependentName(f.getAbsolutePath());

//        PathManager.get
        return FileUtil.toSystemIndependentName(PathManager.getHomePath() + "/testData");
    }

    private static final Logger LOG = Logger.getInstance("org.jetbrains.plugins.clojure.util.TestUtils");

    private static final String[] RUN_PATHS = {
            "out/test/clojure-plugin",
// if tests are run using ant script
            "dist/testClasses"};

    private static String TEST_DATA_PATH;

    public static final String CARET_MARKER = "<caret>";
    public static final String BEGIN_MARKER = "<begin>";
    public static final String END_MARKER = "<end>";

    public static String getTestDataPath() {
        if (TEST_DATA_PATH == null) {
            ClassLoader loader = TestUtils2.class.getClassLoader();
            URL resource = loader.getResource("testData");
            try {
                TEST_DATA_PATH = new File("testData").getAbsolutePath();
                if (resource != null) {
                    TEST_DATA_PATH = new File(resource.toURI()).getPath().replace(File.separatorChar, '/');
                }
            } catch (URISyntaxException e) {
                LOG.error(e);
                return null;
            }
        }
        return TEST_DATA_PATH;
    }

    public static String getMockJdk() {
        return getTestDataPath() + "/mockJDK";
    }


    public static String getMockClojureLib() {
        return getTestDataPath() + "/mockClojureLib/clojure-1.5.jar";
    }

    public static String getMockClojureContribLib() {
        return getTestDataPath() + "/mockClojureLib/clojure-contrib.jar";
    }

    @Nullable
    public static String getDataPath(@NotNull Class<?> clazz) {
        final String classDir = getClassRelativePath(clazz);
        String moduleDir = getModulePath(clazz);
        return classDir != null && moduleDir != null ? moduleDir + "/" + classDir + "/data/" : null;
    }

    public static String getOutputPath(final Class<?> clazz) {
        final String classDir = getClassRelativePath(clazz);
        String moduleDir = getModulePath(clazz);
        return classDir != null && moduleDir != null ? moduleDir + "/" + classDir + "/output/" : null;
    }

    @Nullable
    public static String getDataPath(@NotNull Class<?> s, @NotNull final String relativePath) {
        return getDataPath(s) + "/" + relativePath;
    }

    @Nullable
    public static String getClassRelativePath(@NotNull Class<?> s) {
        String classFullPath = getClassFullPath(s);
        for (String path : RUN_PATHS) {
            final String dataPath = getClassDirPath(classFullPath, path);
            if (dataPath != null) {
                return dataPath;
            }
        }
        return null;
    }

    @Nullable
    public static String getModulePath(@NotNull Class<?> s) {
        String classFullPath = getClassFullPath(s);
        for (String path : RUN_PATHS) {
            final String dataPath = getModulePath(classFullPath, path);
            if (dataPath != null) {
                return dataPath;
            }
        }
        return null;
    }

    public static String getClassFullPath(@NotNull final Class<?> s) {
        String name = s.getSimpleName() + ".class";
        final URL url = s.getResource(name);
        return url.getPath();
    }

    @Nullable
    private static String getModulePath(@NotNull String s, @NotNull final String indicator) {
        int n = s.indexOf(indicator);
        if (n == -1) {
            return null;
        }
        return s.substring(0, n - 1);
    }

    @Nullable
    private static String getClassDirPath(@NotNull String s, @NotNull final String indicator) {
        int n = s.indexOf(indicator);
        if (n == -1) {
            return null;
        }
        s = "test" + s.substring(n + indicator.length());
        s = s.substring(0, s.lastIndexOf('/'));
        return s;
    }

    public static ModifiableRootModel addLibrary(ModifiableRootModel rootModel,
                                                 ModuleRootManager rootManager, OrderEnumerator libs,
                                                 List<Library.ModifiableModel> libModels,
                                                 final String clojureLibraryName, String mockLib, String mockLibSrc) {
        class CustomProcessor implements Processor<Library> {
            private boolean result = true;

            public boolean process(Library library) {
                boolean res = library.getName().equals(clojureLibraryName);
                if (res) result = false;
                return result;
            }
        }
        CustomProcessor processor = new CustomProcessor();
        libs.forEachLibrary(processor);
        if (processor.result) {
            if (rootModel == null) {
                rootModel = rootManager.getModifiableModel();
            }
            final LibraryTable libraryTable = rootModel.getModuleLibraryTable();
            final Library scalaLib = libraryTable.createLibrary(clojureLibraryName);
            final Library.ModifiableModel libModel = scalaLib.getModifiableModel();
            libModels.add(libModel);
            addLibraryRoots(libModel, mockLib, mockLibSrc);
        }
        return rootModel;
    }

    public static void addLibraryRoots(Library.ModifiableModel libModel, String mockLib, String mockLibSrc) {
        final File libRoot = new File(mockLib);
        assert libRoot.exists();

        libModel.addRoot(VfsUtil.getUrlForLibraryRoot(libRoot), OrderRootType.CLASSES);
        if (mockLibSrc != null) {
            final File srcRoot = new File(mockLibSrc);
            assert srcRoot.exists();
            libModel.addRoot(VfsUtil.getUrlForLibraryRoot(srcRoot), OrderRootType.SOURCES);
        }
//        ((VirtualFilePointerManagerImpl) VirtualFilePointerManager.getInstance()).storePointers();
    }

}

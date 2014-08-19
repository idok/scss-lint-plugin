//package com.eslint.utils;
//
//import com.eslint.config.schema.BaseType;
//import com.eslint.config.schema.ESLintSchema;
//import com.eslint.config.schema.RuntimeTypeAdapterFactory;
//import com.eslint.config.schema.SchemaJsonObject;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import org.junit.Test;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.Writer;
//
//public class ESLintOptionsTest {
////    public static final Gson GSON = GsonUtil.createDefaultBuilder().create();
//
////    @Test
////    public void testMultiply() throws IOException {
////        Writer writer = null;
////        try {
////            writer = new FileWriter("/Users/idok/Projects/eslint-plugin/temp/Output.json");
////            Gson gson = new GsonBuilder().setPrettyPrinting().create();
////            ESLintOption o = new ESLintOption();
////            o.name = "koko";
////            gson.toJson(o, writer);
////        } catch (IOException e) {
////            e.printStackTrace();
////        } finally {
////            if (writer != null) {
////                writer.close();
////            }
////        }
////    }
//
////    @Test
////    public void loadJson() throws IOException {
////        FileReader reader = null;
////        try {
////            reader = new FileReader("/Users/idok/Projects/eslint-plugin/temp/Output.json");
////            Gson gson = new GsonBuilder().setPrettyPrinting().create();
////            ESLintOption ret = gson.fromJson(reader, ESLintOption.class);
////        } catch (IOException e) {
////            e.printStackTrace();
////        } finally {
////            if (reader != null) {
////                reader.close();
////            }
////        }
////    }
//
//    String schema = "/Users/idok/Projects/eslint-plugin/src/com/eslint/config/schema.json";
//
////    @Test
////    public void loadJsonSchema() throws IOException {
////        FileReader reader = null;
////        try {
////            reader = new FileReader(schema);
////            Gson gson = new GsonBuilder().setPrettyPrinting().create();
////            ESLintSchema ret = gson.fromJson(reader, ESLintSchema.class);
////            System.out.println(ret.description);
////        } catch (IOException e) {
////            e.printStackTrace();
////        } finally {
////            if (reader != null) {
////                reader.close();
////            }
////        }
////    }
//
//    @Test
//    public void save() throws IOException {
////        toFile(ESLintSchema.ROOT, "/Users/idok/Projects/eslint-plugin/temp/Output2.json");
////        RuleCache.initializeFromPath(null, null);
////        ESLintSchema.buildSchema();
//        RuntimeTypeAdapterFactory<BaseType> adapter = RuntimeTypeAdapterFactory.of(BaseType.class, "class-type")
//                .registerSubtype(SchemaJsonObject.class)
//                .registerSubtype(BaseType.class);
////        Gson gson = ESLintSchema.getGson();
//        GsonBuilder builder = new GsonBuilder();
//        builder.registerTypeAdapterFactory(adapter);
//        Gson gson = builder.setPrettyPrinting().create();
//        gson.toJson(ESLintSchema.ROOT, System.out);
////        toConsole(ESLintSchema.ROOT);
//    }
//
//    public static void toConsole(Object obj) {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        gson.toJson(obj, System.out);
//    }
//
//    public static void toFile(Object obj, String path) {
//        Writer writer = null;
//        try {
//            writer = new FileWriter(path);
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            gson.toJson(obj, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (writer != null) {
//                try {
//                    writer.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}

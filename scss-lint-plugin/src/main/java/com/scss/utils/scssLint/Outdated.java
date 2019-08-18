//package com.scss.utils.scssLint;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.annotations.SerializedName;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.reflect.Type;
//import java.util.List;
//import java.util.Map;
//
//public class Outdated {
//    @SerializedName("react-templates")
//    public OutdatedClass rt;
//
//    public static Outdated parseNpmOutdated(String json) {
//        GsonBuilder builder = new GsonBuilder();
////        builder.registerTypeAdapterFactory(adapter);
//        Gson g = builder.setPrettyPrinting().create();
//        Type listType = new TypeToken<Outdated>() {}.getType();
//        return g.fromJson(json, listType);
//    }
//
//    public static class OutdatedClass {
//        public String current;
//        public String wanted;
//        public String latest;
//        public String location;
//    }
//
//    final static String json = "{" +
//            "\"scss-lint-plugin/testData/one.scss\": [" +
//            "{\"line\": 5,\"column\": 3,\"length\": 22,\"severity\": \"warning\",\"reason\": \"Properties should be ordered color, font\",\"linter\": \"PropertySortOrder\"}" +
//            "]" +
//            "}";
//
//    public static class Koko {
////        @SerializedName("files")
////        public List<Map<String,String>> files;
//        public Map<String, List<Obj>> files;
//    }
//
//    public static class Obj {
//        public String line;
//        public int column;
//        public int length;
//        public String severity;
//        public String reason;
//        public String linter;
//    }
//
//    public static  Map<String, List<Obj>> parseNpmOutdated2(String json) {
//        GsonBuilder builder = new GsonBuilder();
////        builder.registerTypeAdapterFactory(adapter);
//        Gson g = builder.setPrettyPrinting().create();
//        Type listType = new TypeToken< Map<String, List<Obj>>>() {}.getType();
//        return g.fromJson(json, listType);
//    }
//
//
//    public static void main(String[] args) {
//        Map<String, List<Obj>> koko = parseNpmOutdated2(json);
//        System.out.println(koko);
//    }
//
//
//}
//

package com.scss.utils.scssLint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Lint {
//    public File file;
//
//    public static Lint read(String xml) {
//        XStream xstream = new XStream();
//        xstream.alias("lint", Lint.class);
//        xstream.alias("file", File.class);
//        xstream.alias("issue", Issue.class);
//        xstream.addImplicitCollection(File.class, "issues");
//        xstream.useAttributeFor(File.class, "name");
//        xstream.useAttributeFor(Issue.class, "linter");
//        xstream.useAttributeFor(Issue.class, "line");
//        xstream.useAttributeFor(Issue.class, "column");
//        xstream.useAttributeFor(Issue.class, "length");
//        xstream.useAttributeFor(Issue.class, "severity");
//        xstream.useAttributeFor(Issue.class, "reason");
//        return (Lint) xstream.fromXML(xml);
//    }
//
//    public static class File {
//        public String name;
//        public List<Issue> issues = new ArrayList<Issue>();
//    }

    public static Map<String, List<Issue>> parse(String json) {
        GsonBuilder builder = new GsonBuilder();
//        builder.registerTypeAdapterFactory(adapter);
        Gson g = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<Map<String, List<Issue>>>() {}.getType();
        return g.fromJson(json, listType);
    }

    public static class Issue {
        public String linter;
        public int line;
        public int column;
        public int length;
        public String severity;
        public String reason;
    }
}


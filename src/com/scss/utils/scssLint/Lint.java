package com.scss.utils.scssLint;

import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;
import java.util.List;

public class Lint {
    public File file;

    public static Lint read(String xml) {
        XStream xstream = new XStream();
        xstream.alias("lint", Lint.class);
        xstream.alias("file", File.class);
        xstream.alias("issue", Issue.class);
        xstream.addImplicitCollection(File.class, "issues");
        xstream.useAttributeFor(File.class, "name");
        xstream.useAttributeFor(Issue.class, "linter");
        xstream.useAttributeFor(Issue.class, "line");
        xstream.useAttributeFor(Issue.class, "column");
        xstream.useAttributeFor(Issue.class, "length");
        xstream.useAttributeFor(Issue.class, "severity");
        xstream.useAttributeFor(Issue.class, "reason");
        return (Lint) xstream.fromXML(xml);
    }

    public static class File {
        public String name;
        public List<Issue> issues = new ArrayList<Issue>();
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


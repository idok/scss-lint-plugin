package com.wix.utils;

public final class Strings {
    private Strings() {
    }

    public static boolean areEqual(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }
}

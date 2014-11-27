package com.scss.annotator;

import com.intellij.psi.PsiElement;

public final class Fixes {
    private Fixes() {
    }

    public static BaseActionFix getFixForRule(String rule, PsiElement element) {
        if (rule.equals("PropertySortOrder")) {
            return new PropertySortOrderFix(element);
        }
        return null;
    }
}
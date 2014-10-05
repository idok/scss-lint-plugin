package com.scss.annotator;

import com.intellij.psi.PsiElement;

public final class Fixes {
    private Fixes() {
    }

    public static BaseActionFix getFixForRule(String rule, PsiElement element) {
//        Map<String, BaseActionFix> map = new HashMap<String, BaseActionFix>();
//        map.put("strict", )
        if (rule.equals("PropertySortOrder")) {
            return new PropertySortOrderFix(element);
        }
        return null;
    }
}
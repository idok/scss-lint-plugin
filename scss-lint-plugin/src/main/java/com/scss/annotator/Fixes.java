package com.scss.annotator;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public final class Fixes {
    private Fixes() {
    }

    @Nullable
    public static BaseActionFix getFixForRule(String rule, PsiElement element) {
        if ("PropertySortOrder".equals(rule)) {
            return new PropertySortOrderFix(element);
        }
        return null;
    }
}
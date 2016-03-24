package com.wix.annotator;

import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.SeverityRegistrar;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AnnotatorUtils {
    private AnnotatorUtils() {
    }

    @NotNull
    public static TextAttributes getTextAttributes(@Nullable EditorColorsScheme editorColorsScheme, @NotNull SeverityRegistrar severityRegistrar, @NotNull HighlightSeverity severity) {
        TextAttributes textAttributes = severityRegistrar.getTextAttributesBySeverity(severity);
        if (textAttributes != null) {
            return textAttributes;
        }
        EditorColorsScheme colorsScheme = getColorsScheme(editorColorsScheme);
        HighlightInfoType.HighlightInfoTypeImpl infoType = severityRegistrar.getHighlightInfoTypeBySeverity(severity);
        TextAttributesKey key = infoType.getAttributesKey();
        return colorsScheme.getAttributes(key);
    }

    @NotNull
    private static EditorColorsScheme getColorsScheme(@Nullable EditorColorsScheme customScheme) {
        return customScheme == null ? EditorColorsManager.getInstance().getGlobalScheme() : customScheme;
    }
}

package com.scss.annotator;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.css.CssBlock;
import com.intellij.psi.css.CssDeclaration;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import com.scss.ScssLintBundle;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * @author idok
 */
public class PropertySortOrderFix extends BaseActionFix {

    public PropertySortOrderFix(PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public String getText() {
        return ScssLintBundle.message("inspection.fix.sort");
    }

    @Override
    public void fix(@NotNull Project project, Editor editor, PsiFile file, PsiElement start) throws IncorrectOperationException {
        CssBlock block = PsiTreeUtil.getParentOfType(start, CssBlock.class);
        CssDeclaration declarations[] = block.getDeclarations();
        CssDeclaration sorted[] = new CssDeclaration[declarations.length];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = (CssDeclaration) declarations[i].copy();
        }

        ContainerUtil.sort(sorted, new Comparator<CssDeclaration>() {
            @Override
            public int compare(CssDeclaration cssDeclaration, CssDeclaration cssDeclaration2) {
                return cssDeclaration.getPropertyName().compareTo(cssDeclaration2.getPropertyName());
            }
        });

        for (int i = 0; i < declarations.length; i++) {
            declarations[i].replace(sorted[i]);
        }
    }
}

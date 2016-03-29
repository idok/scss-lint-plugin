//package com.scss.annotator;
//
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.project.Project;
//import com.intellij.psi.PsiElement;
//import com.intellij.psi.PsiFile;
//import com.intellij.psi.css.CssBlock;
//import com.intellij.psi.css.CssDeclaration;
//import com.intellij.psi.css.impl.parsing.CssParser;
//import com.intellij.psi.css.impl.util.table.CssPropertyUtil;
//import com.intellij.psi.util.PsiTreeUtil;
//import com.intellij.util.IncorrectOperationException;
//import com.intellij.util.containers.ContainerUtil;
//import com.scss.ScssLintBundle;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Comparator;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * @author idok
// */
//public class PropertySortOrderFix extends BaseActionFix {
//
//    private static final String[] VENDOR_PREFIXS = {
//            "-moz-"     /* Firefox and other browsers using Mozilla's browser engine */,
//            "-webkit-"  /* Safari, Chrome and browsers using the Webkit engine */,
//            "-o-"       /* Opera */,
//            "-ms-"      /* Internet Explorer (but not always) */};
//
//    private static final Pattern COMPILE = Pattern.compile("-(?:moz|o|webkit|ms)-(font)");
//
//    public PropertySortOrderFix(PsiElement element) {
//        super(element);
//    }
//
//    @NotNull
//    @Override
//    public String getText() {
//        return ScssLintBundle.message("inspection.fix.sort");
//    }
//
//    public static String strip(String p) {
//        Matcher m = COMPILE.matcher(p);
//        return m.matches() ? m.group(1) : p;
//    }
//
//    @Override
//    public void fix(@NotNull Project project, Editor editor, PsiFile file, PsiElement start) throws IncorrectOperationException {
//        CssBlock block = PsiTreeUtil.getParentOfType(start, CssBlock.class);
//        CssDeclaration[] declarations = block.getDeclarations();
//        CssDeclaration[] sorted = new CssDeclaration[declarations.length];
//        for (int i = 0; i < sorted.length; i++) {
//            sorted[i] = (CssDeclaration) declarations[i].copy();
//        }
//
//        ContainerUtil.sort(sorted, new Comparator<CssDeclaration>() {
//            @Override
//            public int compare(CssDeclaration cssDeclaration, CssDeclaration cssDeclaration2) {
//                String p1 = CssPropertyUtil.getElementNameWithoutVendorPrefix(cssDeclaration.getPropertyName());
//                String v1 = CssPropertyUtil.getVendorPrefix(cssDeclaration.getPropertyName());
//                String p2 = CssPropertyUtil.getElementNameWithoutVendorPrefix(cssDeclaration2.getPropertyName());
//                String v2 = CssPropertyUtil.getVendorPrefix(cssDeclaration2.getPropertyName());
//                return p1.compareTo(p2);
//            }
//        });
//
//        for (int i = 0; i < declarations.length; i++) {
//            declarations[i].replace(sorted[i]);
//        }
//    }
//}

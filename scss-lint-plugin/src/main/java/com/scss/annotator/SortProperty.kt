//package com.scss.annotator
//
//import com.intellij.openapi.editor.Editor
//import com.intellij.openapi.project.Project
//import com.intellij.psi.PsiElement
//import com.intellij.psi.PsiFile
//import com.intellij.psi.css.CssBlock
//import com.intellij.psi.css.CssDeclaration
////import com.intellij.psi.css.impl.util.table.CssDescriptorsUtil //CssPropertyUtil
//import com.intellij.psi.util.PsiTreeUtil
//import com.intellij.util.IncorrectOperationException
//import com.scss.ScssLintBundle
//import java.util.*
//import java.util.regex.Pattern
//
///**
// * @author idok
// */
//class PropertySortOrderFix(element: PsiElement) : BaseActionFix(element) {
//
//    override fun getText(): String {
//        return ScssLintBundle.message("inspection.fix.sort")
//    }
//
//    @Throws(IncorrectOperationException::class)
//    public override fun fix(project: Project, editor: Editor, file: PsiFile, start: PsiElement) {
//        val block = PsiTreeUtil.getParentOfType(start, CssBlock::class.java) ?: return
//        val declarations = block.declarations
//        val cloned:List<CssDeclaration> = declarations.map { it.copy() as CssDeclaration }
//        val sorted = cloned.sortedWith(CssDeclarationComparator())
//        for (i in declarations.indices) {
//            declarations[i].replace(sorted[i])
//        }
//    }
//
//    companion object {
//        private val COMPILE = Pattern.compile("-(?:moz|o|webkit|ms)-(font)")
//
//        fun strip(p: String): String {
//            val m = COMPILE.matcher(p)
//            return if (m.matches()) m.group(1) else p
//        }
//
//        fun parse(it: CssDeclaration?) = parse(it?.propertyName ?: "")
////        fun parse(it: String) = CP2(CssPropertyUtil.getVendorPrefix(it), CssPropertyUtil.getElementNameWithoutVendorPrefix(it))
//        fun parse(it: String) = CP2("", "")
//    }
//
//    class CssDeclarationComparator : Comparator<CssDeclaration> {
//        override fun compare(o1: CssDeclaration?, o2: CssDeclaration?): Int {
//            val p1 = parse(o1)
//            val p2 = parse(o2)
//            return p1.compareTo(p2)
//        }
//    }
//
//    data class CP2(val prefix: String, val name: String) {
//        override fun toString() = "$prefix$name"
//
//        fun compareTo(o1: CP2): Int {
//            return name.compareTo(o1.name)
//        }
//    }
//}
//
//

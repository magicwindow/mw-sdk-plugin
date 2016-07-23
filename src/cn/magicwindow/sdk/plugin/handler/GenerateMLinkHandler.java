package cn.magicwindow.sdk.plugin.handler;

import cn.magicwindow.sdk.plugin.PluginUtils;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;

/**
 * Created by tony on 16/7/23.
 */
public class GenerateMLinkHandler extends EditorWriteActionHandler {

    public static final int MLINK_TYPE = 1;
    public static final int MLINK_DEFAULT_TYPE = 2;
    private int mLinkType = 0;

    public GenerateMLinkHandler(int type) {
        this.mLinkType = type;
    }

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {

        PsiJavaFile psiJavaFile = null;
        try {
            psiJavaFile = (PsiJavaFile) dataContext.getData(CommonDataKeys.PSI_FILE.getName());
        } catch (java.lang.ClassCastException e) {
            e.printStackTrace();
            Messages.showInfoMessage("无法在该文件上使用mLink Annotation","不能生成注解");
            return;
        }

        if (psiJavaFile == null) {
            return;
        }

        Project project = psiJavaFile.getProject();
        PsiClass mClass = getCurrentPsiClass(psiJavaFile);
        if (mClass == null) {
            return;
        }

        PsiClass psiClass = null;
        if (mLinkType == MLINK_TYPE) {
            psiClass = PluginUtils.getClassForProject(project,"com.zxinsight.mlink.annotation.MLinkRouter");
        } else if (mLinkType == MLINK_DEFAULT_TYPE) {
            psiClass = PluginUtils.getClassForProject(project,"com.zxinsight.mlink.annotation.MLinkDefaultRouter");
        }

        if (psiClass == null) {
            PluginUtils.showErrorNotification(project,"请先下载sdk");
            return;
        }
        psiJavaFile.importClass(psiClass);

        PsiModifierList modifierList = mClass.getModifierList();
        if (mLinkType == MLINK_TYPE) {
            modifierList.addAnnotation("MLinkRouter(keys={})");
        } else if (mLinkType == MLINK_DEFAULT_TYPE) {
            modifierList.addAnnotation("MLinkDefaultRouter");
        }
    }

    public static PsiClass getCurrentPsiClass(PsiJavaFile psiJavaFile) {
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
            if (!PsiUtil.isInnerClass(psiClass) && !PsiUtil.isAbstractClass(psiClass)) {
                return psiClass;
            }
        }

        return null;
    }
}

package cn.magicwindow.sdk.plugin.action;


import cn.magicwindow.sdk.plugin.ui.OneStopSolutionEntrance;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

/**
 * 一站式方案, 包括初始化sdk、初始化mLink、在Activity中标注MLink注解、在proguard中keep掉魔窗sdk
 * Created by tony on 16/8/2.
 */
public class OneStopSolutionAction extends BaseAction {

    @Override
    public void update(AnActionEvent e) {
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        OneStopSolutionEntrance entrance = new OneStopSolutionEntrance(psiClass, editor, project);
        entrance.firstFram();
    }
}

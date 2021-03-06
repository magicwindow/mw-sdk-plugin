package cn.magicwindow.sdk.plugin.action;

import cn.magicwindow.sdk.plugin.CodeGenerator;
import com.intellij.openapi.actionSystem.*;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

/**
 * Created by tony on 16/6/2.
 */
public class InitMLinkAction extends BaseAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);

        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        final SelectionModel selectionModel = editor.getSelectionModel();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                new CodeGenerator(psiClass).generateMLinkConfig();
            }
        };

        WriteCommandAction.runWriteCommandAction(project, runnable);
        selectionModel.removeSelection();
    }
}

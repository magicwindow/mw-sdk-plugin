package cn.magicwindow.sdk.plugin.action;

import cn.magicwindow.sdk.plugin.CodeGenerator;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;

/**
 * Created by tony on 16/6/14.
 */
public class InitMagicWindowConfigAction extends BaseAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);

        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        final String channel = Messages.showInputDialog(project,
                "您的渠道号", "Fill in the channel",
                Messages.getQuestionIcon());

        final SelectionModel selectionModel = editor.getSelectionModel();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                new CodeGenerator(psiClass).generateInitMWConfig(channel);
            }
        };

        WriteCommandAction.runWriteCommandAction(project, runnable);
        selectionModel.removeSelection();
    }
}

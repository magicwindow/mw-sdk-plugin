package cn.magicwindow.sdk.plugin.action;

import cn.magicwindow.sdk.plugin.CodeGenerator;
import cn.magicwindow.sdk.plugin.ui.dialog.ConfigDialog;
import cn.magicwindow.sdk.plugin.PluginUtils;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;

/**
 * Created by tony on 16/6/14.
 */
public class InitMagicWindowConfigAction extends BaseAction {

    private String channel;
    private String ak;

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);

        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        ConfigDialog dialog = new ConfigDialog(project,"初始化魔窗sdk配置");
        dialog.show();

        if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
            return;
        }

        channel = dialog.getChannel();

        if (channel == null) {
            PluginUtils.showErrorNotification(project, "为了便于数据统计, 渠道号不能为空");
            return;
        }

        ak = dialog.getAk();

        if (ak == null) {
            PluginUtils.showErrorNotification(project, "魔窗的AppKey 不能为空");
            return;
        }

        SelectionModel selectionModel = editor.getSelectionModel();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                new CodeGenerator(psiClass).generateInitMWConfig(channel,ak);
            }
        };

        WriteCommandAction.runWriteCommandAction(project, runnable);
        selectionModel.removeSelection();
    }

}

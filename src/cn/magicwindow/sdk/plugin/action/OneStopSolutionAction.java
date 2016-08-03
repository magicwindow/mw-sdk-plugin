package cn.magicwindow.sdk.plugin.action;


import cn.magicwindow.sdk.plugin.CodeGenerator;
import cn.magicwindow.sdk.plugin.PluginUtils;
import cn.magicwindow.sdk.plugin.dialog.ConfigDialog;
import cn.magicwindow.sdk.plugin.dialog.OneStopSolutionDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;

/**
 * 一站式方案
 * Created by tony on 16/8/2.
 */
public class OneStopSolutionAction extends BaseAction {

    private String channel;
    private String ak;

    @Override
    public void update(AnActionEvent e) {
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);

        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        OneStopSolutionDialog dialog = new OneStopSolutionDialog(project,"一站式初始化魔窗sdk配置");
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
                new CodeGenerator(psiClass).generateAll(channel,ak);
            }
        };

        WriteCommandAction.runWriteCommandAction(project, runnable);
        selectionModel.removeSelection();
    }
}

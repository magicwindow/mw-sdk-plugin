package cn.magicwindow.sdk.plugin.action;


import cn.magicwindow.sdk.plugin.CodeGenerator;
import cn.magicwindow.sdk.plugin.PluginUtils;
import cn.magicwindow.sdk.plugin.Preconditions;
import cn.magicwindow.sdk.plugin.dialog.ConfigDialog;
import cn.magicwindow.sdk.plugin.dialog.OneStopSolutionDialog;
import cn.magicwindow.sdk.plugin.form.EntryList;
import cn.magicwindow.sdk.plugin.form.ICancelListener;
import cn.magicwindow.sdk.plugin.form.IConfirmListener;
import cn.magicwindow.sdk.plugin.model.ActivityEntry;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 一站式方案
 * Created by tony on 16/8/2.
 */
public class OneStopSolutionAction extends BaseAction implements IConfirmListener,ICancelListener {

    private String channel;
    private String ak;
    private CodeGenerator codeGenerator;
    private JFrame mDialog;

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
                codeGenerator = new CodeGenerator(psiClass);
                codeGenerator.generateAll(channel,ak);
            }
        };

        WriteCommandAction.runWriteCommandAction(project, runnable);
        selectionModel.removeSelection();

        openDialog(project, editor);
    }

    private void openDialog(Project project,Editor editor) {
        if (Preconditions.isNotBlank(codeGenerator.getActivities())) {
            EntryList panel = new EntryList(project, editor,codeGenerator.getActivities(),this,this);

            mDialog = new JFrame();
            mDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            mDialog.getRootPane().setDefaultButton(panel.getConfirmButton());
            mDialog.getContentPane().add(panel);
            mDialog.pack();
            mDialog.setLocationRelativeTo(null);
            mDialog.setVisible(true);
        }
    }

    private void closeDialog() {
        if (mDialog == null) {
            return;
        }

        mDialog.setVisible(false);
        mDialog.dispose();
    }

    @Override
    public void onConfirm(Project project, Editor editor,List<ActivityEntry> activities) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }

        if (Preconditions.isNotBlank(activities)) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    codeGenerator.generateMLinkAnnotation(activities);
                }
            };

            WriteCommandAction.runWriteCommandAction(project, runnable);
        } else {
            PluginUtils.showInfoNotification(project,"无需在Activity中添加@MLinkRouter");
        }

        closeDialog();
    }

    @Override
    public void onCancel() {
        closeDialog();
    }
}

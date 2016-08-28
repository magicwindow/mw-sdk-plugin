package cn.magicwindow.sdk.plugin.form;

import cn.magicwindow.sdk.plugin.CodeGenerator;
import cn.magicwindow.sdk.plugin.PluginUtils;
import cn.magicwindow.sdk.plugin.Preconditions;
import cn.magicwindow.sdk.plugin.model.ActivityEntry;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by tony on 16/8/27.
 */
public class OneStopSolutionEntrance implements ActionListener,IConfirmListener,ICancelListener {

    private JFrame firstFram,secondFram;
    private JPanel firstPanel;
    private JTextField channelTextField,akTextField;
    private String channel,ak;
    private CodeGenerator codeGenerator;
    private PsiClass psiClass;
    private Editor editor;
    private Project project;

    public OneStopSolutionEntrance(PsiClass psiClass, Editor editor, Project project) {
        this.psiClass = psiClass;
        this.editor = editor;
        this.project = project;
    }

    public void firstFram(){
        firstFram = new JFrame("一站式初始化魔窗sdk配置");
        firstFram.setSize(500,400);
        firstFram.setDefaultCloseOperation(3);
        firstFram.setResizable(false);
        firstFram.setLocationRelativeTo(null);

        firstPanel = new JPanel();
        JLabel iconLabel = new JLabel(Messages.getQuestionIcon());
        Container container = new Container();
        container.setLayout(new BorderLayout());
        container.add(iconLabel, BorderLayout.NORTH);
        firstPanel.add(container, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JPanel channelPanel = new JPanel(new FlowLayout());
        JLabel channelLabel = new JLabel("  您的渠道号:");
        channelPanel.add(channelLabel);
        channelTextField = new JTextField(20);
        channelTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                channel = channelTextField.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        channelPanel.add(channelTextField);
        rightPanel.add(channelPanel, BorderLayout.PAGE_START);

        JPanel akPanel = new JPanel(new FlowLayout());
        JLabel akLabel = new JLabel("您的Appkey:");
        akPanel.add(akLabel);
        akTextField = new JTextField(20);
        akTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                ak = akTextField.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        akPanel.add(akTextField);

        rightPanel.add(akPanel, BorderLayout.PAGE_END);

        firstPanel.add(rightPanel, BorderLayout.CENTER);

        JButton next = new JButton("Next");
        next.setName("next");
        next.setBounds(250, 320, 75, 26);
        next.addActionListener(this);

        firstFram.add(next);
        firstFram.add(firstPanel);
        firstFram.setVisible(true);
    }

    private void secondFram(){
        secondFram = new JFrame();
        secondFram.setSize(500,400);
        secondFram.setDefaultCloseOperation(3);
        secondFram.setResizable(false);

        EntryList panel = new EntryList(project, editor,codeGenerator.getActivities(),this,this);

        secondFram.getContentPane().add(panel);
        secondFram.pack();
        secondFram.setLocationRelativeTo(null);
        secondFram.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        JButton bt = (JButton) e.getSource();

        if (bt.getName() == "next") {

            if (channel == null) {
                PluginUtils.showErrorNotification(project, "为了便于数据统计, 渠道号不能为空");
                return;
            }

            if (ak == null) {
                PluginUtils.showErrorNotification(project, "魔窗的AppKey 不能为空");
                return;
            }

            firstFram.removeAll();
            firstFram.dispose();

            codeGenerator = new CodeGenerator(psiClass);
            codeGenerator.generateAll(channel, ak);

            secondFram();
        }
    }

    private void closeDialog() {
        if (secondFram == null) {
            return;
        }

        secondFram.setVisible(false);
        secondFram.dispose();
    }

    @Override
    public void onConfirm(Project project, Editor editor, java.util.List<ActivityEntry> activities) {
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
        secondFram.removeAll();
        secondFram.dispose();
        firstFram();
        channelTextField.setText(channel);
        akTextField.setText(ak);
    }
}

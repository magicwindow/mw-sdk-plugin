package cn.magicwindow.sdk.plugin.form;

import cn.magicwindow.sdk.plugin.Preconditions;
import cn.magicwindow.sdk.plugin.model.ActivityEntry;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tony on 16/8/4.
 */
public class EntryList extends JPanel {

    private Project mProject;
    private Editor mEditor;

    private EntryHeader mEntryHeader;
    private JButton mConfirm;
    private JButton mCancel;

    private IConfirmListener mConfirmListener;
    private ICancelListener mCancelListener;

    private List<ActivityEntry> mActivities;
    private ArrayList<Entry> mEntries = new ArrayList<Entry>();
    private List<ActivityEntry> checkedList = new ArrayList<ActivityEntry>();

    public EntryList(Project project, Editor editor, List<ActivityEntry> activities, IConfirmListener confirmListener, ICancelListener cancelListener) {
        mProject = project;
        mEditor = editor;
        mActivities = activities;

        mConfirmListener = confirmListener;
        mCancelListener = cancelListener;

        setPreferredSize(new Dimension(640, 360));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        addInjections();
        addButtons();
    }

    private void addInjections() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mEntryHeader = new EntryHeader();
        contentPanel.add(mEntryHeader);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel injectionsPanel = new JPanel();
        injectionsPanel.setLayout(new BoxLayout(injectionsPanel, BoxLayout.PAGE_AXIS));
        injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        int cnt = 0;
        String name = null;
        for (ActivityEntry activity : mActivities) {
            int last = activity.getName().lastIndexOf(".");
            if (last>-1) {
                name = activity.getName().substring(last+1);
            }
            Entry entry = new Entry(this, activity, name);

            if (cnt > 0) {
                injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            injectionsPanel.add(entry);
            cnt++;

            mEntries.add(entry);
        }
        injectionsPanel.add(Box.createVerticalGlue());
        injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JBScrollPane scrollPane = new JBScrollPane(injectionsPanel);
        contentPanel.add(scrollPane);

        add(contentPanel, BorderLayout.CENTER);
        refresh();
    }

    /**
     * 底部的按钮
     */
    private void addButtons() {

        JPanel holderPanel = new JPanel();
        holderPanel.setLayout(new BoxLayout(holderPanel, BoxLayout.LINE_AXIS));
        holderPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        holderPanel.add(Box.createHorizontalGlue());
        add(holderPanel, BorderLayout.PAGE_END);

        mCancel = new JButton();
        mCancel.setAction(new CancelAction());
        mCancel.setPreferredSize(new Dimension(120, 26));
        mCancel.setText("Cancel");
        mCancel.setVisible(true);

        mConfirm = new JButton();
        mConfirm.setAction(new ConfirmAction());
        mConfirm.setPreferredSize(new Dimension(120, 26));
        mConfirm.setText("Confirm");
        mConfirm.setVisible(true);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(mCancel);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(mConfirm);

        add(buttonPanel, BorderLayout.PAGE_END);
        refresh();
    }

    private void refresh() {
        revalidate();

        if (mConfirm != null && mActivities!=null) {
            mConfirm.setVisible(mActivities.size() > 0);
        }
    }

    private boolean checkValidity() {

        for (ActivityEntry entry : mActivities) {
            if (!entry.checkValidity()) {
                return false;
            }
        }

        return true;
    }

    public JButton getConfirmButton() {
        return mConfirm;
    }

    private class ConfirmAction extends AbstractAction {

        public void actionPerformed(ActionEvent event) {
            boolean valid = checkValidity();

            for (Entry entry : mEntries) {
                entry.syncActivityEntry();
            }

            if (valid) {
                if (mConfirmListener != null) {
                    checkedList.clear();

                    for (ActivityEntry entry : mActivities) {
                        if (entry.isClick && entry.checkValidity()) {
                            checkedList.add(entry);
                        }
                    }
                    mConfirmListener.onConfirm(mProject, mEditor,checkedList);
                }
            }
        }
    }

    private class CancelAction extends AbstractAction {

        public void actionPerformed(ActionEvent event) {
            if (mCancelListener != null) {
                mCancelListener.onCancel();
            }
        }
    }
}

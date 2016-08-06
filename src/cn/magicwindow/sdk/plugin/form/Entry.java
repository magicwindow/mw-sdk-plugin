package cn.magicwindow.sdk.plugin.form;

import cn.magicwindow.sdk.plugin.model.ActivityEntry;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

/**
 * Created by tony on 16/8/4.
 */
public class Entry extends JPanel {

    private EntryList mParent;
    private String activityName;
    private ActivityEntry mActivityEntry;

    // ui
    private JLabel mType;
    private JCheckBox mEvent;
    private JTextField mName;
    private Color mNameDefaultColor;
    private Color mNameErrorColor = new Color(0x880000);

    public Entry(EntryList parent, ActivityEntry activityEntry, String name) {

        mParent = parent;
        mActivityEntry = activityEntry;
        activityName = name;

        mEvent = new JCheckBox();
        mEvent.setPreferredSize(new Dimension(100, 26));
        mEvent.addChangeListener(new CheckListener());

        mType = new JLabel(activityName);
        mType.setPreferredSize(new Dimension(140, 26));

        mName = new JTextField("", 10);
        mNameDefaultColor = mName.getBackground();
        mName.setPreferredSize(new Dimension(100, 26));
        mName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                syncActivityEntry();
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(Short.MAX_VALUE, 54));
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mType);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mEvent);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mName);
        add(Box.createHorizontalGlue());

        checkState();
    }

    private void checkState() {
        if (mEvent.isSelected()) {
            mType.setEnabled(true);
            mName.setEnabled(true);
        } else {
            mType.setEnabled(false);
            mName.setEnabled(false);
        }
    }

    public ActivityEntry syncActivityEntry() {
        mActivityEntry.isClick = mEvent.isSelected();
        mActivityEntry.mlinkKey = mName.getText();

        if (mActivityEntry.checkValidity()) {
            mName.setBackground(mNameDefaultColor);
        } else {
            mName.setBackground(mNameErrorColor);
        }

        return mActivityEntry;
    }

    public class CheckListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent event) {
            checkState();
        }
    }
}

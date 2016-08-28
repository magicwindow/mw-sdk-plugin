package cn.magicwindow.sdk.plugin.ui;

import cn.magicwindow.sdk.plugin.model.ActivityEntry;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Created by tony on 16/8/4.
 */
public class Entry extends JPanel {

    private String activityName;
    private ActivityEntry mActivityEntry;

    // ui
    private JLabel activityNameLabel;
    private JCheckBox mMLinkRouter;
    private JTextField mLinkKey;
    private Color mNameDefaultColor;
    private Color mNameErrorColor = new Color(0x880000);

    public Entry(ActivityEntry activityEntry, String name) {

        mActivityEntry = activityEntry;
        activityName = name;

        mMLinkRouter = new JCheckBox();
        mMLinkRouter.setPreferredSize(new Dimension(100, 26));
        mMLinkRouter.addChangeListener(new CheckListener());

        activityNameLabel = new JLabel(activityName);
        activityNameLabel.setPreferredSize(new Dimension(140, 26));

        mLinkKey = new JTextField("", 10);
        mNameDefaultColor = mLinkKey.getBackground();
        mLinkKey.setPreferredSize(new Dimension(100, 26));
        mLinkKey.addFocusListener(new FocusListener() {
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
        add(activityNameLabel);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mMLinkRouter);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mLinkKey);
        add(Box.createHorizontalGlue());

        checkState();
    }

    private void checkState() {
        if (mMLinkRouter.isSelected()) {
            activityNameLabel.setEnabled(true);
            mLinkKey.setEnabled(true);
        } else {
            activityNameLabel.setEnabled(false);
            mLinkKey.setEnabled(false);
        }
    }

    public ActivityEntry syncActivityEntry() {
        mActivityEntry.isClick = mMLinkRouter.isSelected();
        mActivityEntry.mlinkKey = mLinkKey.getText();

        if (mActivityEntry.checkValidity()) {
            mLinkKey.setBackground(mNameDefaultColor);
        } else {
            mLinkKey.setBackground(mNameErrorColor);
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

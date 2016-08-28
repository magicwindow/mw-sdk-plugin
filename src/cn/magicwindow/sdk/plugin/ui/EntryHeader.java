package cn.magicwindow.sdk.plugin.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tony on 16/8/4.
 */
public class EntryHeader extends JPanel {

    protected JLabel activityNameLabel;
    protected JLabel mLinkRouterLabel;
    protected JLabel mLinkDefaultRouterLabel;
    protected JLabel mLinkKeyLabel;

    public EntryHeader() {

        activityNameLabel = new JLabel("Activity Name");
        activityNameLabel.setPreferredSize(new Dimension(120, 26));
        activityNameLabel.setFont(new Font(activityNameLabel.getFont().getFontName(), Font.BOLD, activityNameLabel.getFont().getSize()));

        mLinkRouterLabel = new JLabel("MLinkRouter");
        mLinkRouterLabel.setPreferredSize(new Dimension(90, 26));
        mLinkRouterLabel.setFont(new Font(mLinkRouterLabel.getFont().getFontName(), Font.BOLD, mLinkRouterLabel.getFont().getSize()));

        mLinkDefaultRouterLabel = new JLabel("MLinkDefaultRouter");
        mLinkDefaultRouterLabel.setPreferredSize(new Dimension(140, 26));
        mLinkDefaultRouterLabel.setFont(new Font(mLinkDefaultRouterLabel.getFont().getFontName(), Font.BOLD, mLinkDefaultRouterLabel.getFont().getSize()));

        mLinkKeyLabel = new JLabel("MLink Key");
        mLinkKeyLabel.setPreferredSize(new Dimension(100, 26));
        mLinkKeyLabel.setFont(new Font(mLinkKeyLabel.getFont().getFontName(), Font.BOLD, mLinkKeyLabel.getFont().getSize()));

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(Box.createRigidArea(new Dimension(11, 0)));
        add(activityNameLabel);
        add(Box.createRigidArea(new Dimension(6, 0)));
        add(mLinkRouterLabel);
        add(Box.createRigidArea(new Dimension(12, 0)));
        add(mLinkDefaultRouterLabel);
        add(Box.createRigidArea(new Dimension(22, 0)));
        add(mLinkKeyLabel);
        add(Box.createHorizontalGlue());
    }
}

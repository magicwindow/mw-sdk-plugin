package cn.magicwindow.sdk.plugin.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tony on 16/7/25.
 */
public class AboutUsDialog extends DialogWrapper {

    private Project project;

    public AboutUsDialog(final Project project,
                        String title) {
        super(project, true);
        this.project = project;
        setTitle(title);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        JPanel panel = new JPanel(new BorderLayout(15, 0));

        String path = "/images/mw.png";
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        JLabel iconLabel = new JLabel(icon);
        Container container = new Container();
        container.setLayout(new BorderLayout());
        container.add(iconLabel, BorderLayout.NORTH);
        panel.add(container, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel desc1Panel = new JPanel(new FlowLayout());
        MultilineLabel desc1Label = new MultilineLabel("魔窗（MagicWindow），致力于做创业者最需要、最好用、最贴心的App增长工具，并为创业者构建一个去中心化的高效连接时代，解决App有机增长、生态落地和流量共享的问题！");
        desc1Panel.add(desc1Label);
        rightPanel.add(desc1Panel, BorderLayout.PAGE_START);

        JPanel desc2Panel = new JPanel(new FlowLayout());
        MultilineLabel desc2Label = new MultilineLabel("魔窗（MagicWindow）提供的mLink技术，能够在一天内把App改造成一个本地开放平台，支持把App内任意页面、功能或商品以类似web url的方式进行分发、传播和回流，并提供App内的动态位置管理及App间的一链直达功能，半小时即可完成SDK接入，一天内完成服务化改造！");
        desc2Panel.add(desc2Label);
        rightPanel.add(desc2Panel, BorderLayout.PAGE_END);

        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }

    class MultilineLabel extends JTextArea {

        public MultilineLabel(String s) {
            super(s);
        }

        public void updateUI() {
            super.updateUI();

            // 设置为自动换行
            setLineWrap(true);
            setWrapStyleWord(true);
            setHighlighter(null);
            setEditable(false);
            setSize(500,300);

            // 设置为label的边框，颜色和字体
            LookAndFeel.installBorder(this, "Label.border");

            LookAndFeel.installColorsAndFont(this, "Label.background",
                    "Label.foreground", "Label.font");

        }
    }
}

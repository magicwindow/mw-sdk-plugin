package cn.magicwindow.sdk.plugin.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Created by tony on 16/7/5.
 */
public class ConfigDialog extends DialogWrapper {

    private Project project;
    private String channel;
    private String ak;

    public ConfigDialog(final Project project,
                        String title) {
        super(project, true);
        this.project = project;
        setTitle(title);
        init();
    }

    public Project getProject() {
        return project;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        JLabel iconLabel = new JLabel(Messages.getQuestionIcon());
        Container container = new Container();
        container.setLayout(new BorderLayout());
        container.add(iconLabel, BorderLayout.NORTH);
        panel.add(container, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel channelPanel = new JPanel(new FlowLayout());
        JLabel channelLabel = new JLabel("  您的渠道号:");
        channelPanel.add(channelLabel);
        JTextField channelTextField = new JTextField(20);
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
        JTextField akTextField = new JTextField(20);
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


        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }

    public String getChannel() {
        return channel;
    }

    public String getAk() {
        return ak;
    }
}

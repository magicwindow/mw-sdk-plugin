package cn.magicwindow.sdk.plugin.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;

/**
 * Created by tony on 16/7/23.
 */
public class AboutUsAction extends BaseAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        Messages.showMessageDialog(project,"魔窗（MagicWindow），致力于做创业者最需要、最好用、最贴心的App增长工具，并为创业者构建一个去中心化的高效连接时代，解决App有机增长、生态落地和流量共享的问题！\n" +
                        "\n" +
                        "魔窗（MagicWindow）提供的mLink技术，能够在一天内把App改造成一个本地开放平台，支持把App内任意页面、功能或商品以类似web url的方式进行分发、传播和回流，并提供App内的动态位置管理及App间的一链直达功能，半小时即可完成SDK接入，一天内完成服务化改造！",
                "关于我们",
                Messages.getWarningIcon());
    }
}

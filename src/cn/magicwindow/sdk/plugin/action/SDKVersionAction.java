package cn.magicwindow.sdk.plugin.action;

import cn.magicwindow.sdk.plugin.PluginUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.updateSettings.impl.pluginsAdvertisement.PluginsAdvertiser;
import com.intellij.psi.PsiClass;

/**
 * Created by tony on 16/7/20.
 */
public class SDKVersionAction extends BaseAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        try {
            Class clazz = Class.forName("com.zxinsight.MagicWindowSDK");
        } catch (Exception e1) {
            e1.printStackTrace();
            PluginUtils.showErrorNotification(project,"请先下载sdk");
            return;
        }

        String sdkVersion = com.zxinsight.MagicWindowSDK.getSDKVersion();

        Messages.showMessageDialog(project,sdkVersion,
                "MagicWindow SDK 版本",
                Messages.getWarningIcon());
    }
}

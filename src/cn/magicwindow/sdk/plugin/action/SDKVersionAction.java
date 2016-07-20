package cn.magicwindow.sdk.plugin.action;

import cn.magicwindow.sdk.plugin.PluginUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.updateSettings.impl.pluginsAdvertisement.PluginsAdvertiser;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.EverythingGlobalScope;

/**
 * Created by tony on 16/7/20.
 */
public class SDKVersionAction extends BaseAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        PsiClass mSDK = PluginUtils.isClassAvailableForProject(project,"com.zxinsight.MagicWindowSDK");

        if (mSDK == null) {
            PluginUtils.showErrorNotification(project,"请先下载sdk");
            return;
        }

        PsiMethod[] methods = mSDK.findMethodsByName("getSDKVersion",true);

        String sdkVersion = null;
        if (methods!=null && methods[0]!=null) {
            PsiMethod method = methods[0];
            sdkVersion = getVersion(method.getText());
        }

        Messages.showMessageDialog(project,sdkVersion,
                "MagicWindow SDK 版本",
                Messages.getWarningIcon());
    }

    private String getVersion(String content) {

        if (content!=null) {
            int first = content.indexOf("\"");
            if (first==-1) {
                return null;
            }

            int last = content.indexOf(";");
            if (last==-1 || last==1) {
                return null;
            }
            return content.substring(first+1,last-1);
        }
        return null;
    }
}

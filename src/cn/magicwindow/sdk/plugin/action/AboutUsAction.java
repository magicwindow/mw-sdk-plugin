package cn.magicwindow.sdk.plugin.action;

import cn.magicwindow.sdk.plugin.dialog.AboutUsDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;

/**
 * Created by tony on 16/7/23.
 */
public class AboutUsAction extends BaseAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        AboutUsDialog dialog = new AboutUsDialog(project,"关于我们");
        dialog.show();
    }
}

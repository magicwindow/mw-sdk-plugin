package cn.magicwindow.sdk.plugin;

import cn.magicwindow.sdk.plugin.model.ActivityEntry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by tony on 16/7/5.
 */
public class PluginUtils {

    /**
     * Display simple notification - information
     *
     * @param project
     * @param text
     */
    public static void showInfoNotification(@NotNull Project project,@NotNull String text) {
        showNotification(project, MessageType.INFO, text);
    }

    /**
     * Display simple notification - error
     *
     * @param project
     * @param text
     */
    public static void showErrorNotification(@NotNull Project project,@NotNull String text) {
        showNotification(project, MessageType.ERROR, text);
    }

    /**
     * Display simple notification of given type
     *
     * @param project
     * @param type
     * @param text
     */
    public static void showNotification(Project project, MessageType type, String text) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, type, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }

    public static PsiClass getClassForProject(@NotNull Project project, @NotNull String className) {
        PsiClass classInModule = JavaPsiFacade.getInstance(project).findClass(className,
                new EverythingGlobalScope(project));
        return classInModule;
    }

    /**
     *
     * @return
     */
    public static PsiFile getAndroidManifest(@NotNull PsiClass mClass) {
        PsiDirectory currentDir = mClass.getContainingFile().getContainingDirectory();
        PsiFile result = null;

        if (currentDir!=null) {
            for (int i=0;i<10;i++) {
                if (currentDir.getParentDirectory()!=null && currentDir.getParentDirectory().getName().equals("main")) {
                    currentDir = currentDir.getParentDirectory();
                    result = currentDir.findFile("AndroidManifest.xml");
                    break;
                } else {
                    currentDir = currentDir.getParentDirectory();
                }
            }

        }

        return result;
    }
}

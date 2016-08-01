package cn.magicwindow.sdk.plugin.action;

import cn.magicwindow.sdk.plugin.PluginUtils;
import cn.magicwindow.sdk.plugin.WriteRunnable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;

/**
 * 混淆时排除我们的sdk
 * Created by tony on 16/7/23.
 */
public class KeepMWSDKAction extends BaseAction{

    private static String PROGUARD_PRO = "proguard-rules.pro";

    @Override
    public void update(AnActionEvent e) {
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }

        PsiFile proguardFile = getProguardFile(psiFile);
        if (proguardFile!=null) {
            VirtualFile childFile = proguardFile.getVirtualFile();
            Document document = FileDocumentManager.getInstance().getCachedDocument(childFile);
            if (document != null && document.isWritable()) {
                String proguard = document.getCharsSequence().toString();
                String keepContent = addKeepMWSDK(proguard);

                if (keepContent==null) {
                    return;
                }

                if (proguard==null) {
                    proguard = "";
                }
                proguard += keepContent;
                Runnable writeRunnable = new WriteRunnable(proguard, document);
                ApplicationManager.getApplication().runWriteAction(writeRunnable);
            }
        } else {
            PluginUtils.showErrorNotification(psiFile.getProject(),"找不到proguard-rules.pro文件");
        }
    }

    private PsiFile getProguardFile(PsiFile psiFile) {
        PsiDirectory currentDir = psiFile.getContainingDirectory();
        PsiFile result = null;

        if (currentDir!=null) {
            result = currentDir.findFile(PROGUARD_PRO);
        }
        if (result == null) {
            PsiDirectory defaultDir = currentDir.getParentDirectory();

            for (int i=0;i<10;i++) {
                if (defaultDir.findFile(PROGUARD_PRO)==null) {
                    defaultDir = defaultDir.getParentDirectory();
                } else {
                    break;
                }
            }

            result = defaultDir.findFile(PROGUARD_PRO);
        }

        return result;
    }

    private String addKeepMWSDK(String proguard) {

        StringBuilder result = new StringBuilder();
        if (proguard.indexOf("-keep class com.tencent.mm.sdk.** {*;}")==-1) {
            result.append("\n").append("-keep class com.tencent.mm.sdk.** {*;}");
        }
        if (proguard.indexOf("-keep class com.zxinsight.** {*;}")==-1) {
            result.append("\n").append("-keep class com.zxinsight.** {*;}");
        }
        if (proguard.indexOf("-dontwarn com.zxinsight.**")==-1) {
            result.append("\n").append("-dontwarn com.zxinsight.**");
        }

        return result.toString();
    }
}

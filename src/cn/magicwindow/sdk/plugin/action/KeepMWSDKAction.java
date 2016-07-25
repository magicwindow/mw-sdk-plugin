package cn.magicwindow.sdk.plugin.action;

import cn.magicwindow.sdk.plugin.WriteRunnable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;

/**
 * Created by tony on 16/7/23.
 */
public class KeepMWSDKAction extends BaseAction{

    private static String PROGUARD_PRO = "proguard-rule.pro";

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
                proguard += "-keep class com.tencent.mm.sdk.** {*;}"
                        +"\n"+"-keep class com.zxinsight.** {*;}"
                        +"\n"+"-dontwarn com.zxinsight.**";
                Runnable writeRunnable = new WriteRunnable(proguard, document);
                ApplicationManager.getApplication().runWriteAction(writeRunnable);
            }
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
            if (defaultDir.findFile(PROGUARD_PRO)==null) {
                defaultDir = defaultDir.getParentDirectory();
            }

            if (defaultDir.findFile(PROGUARD_PRO)==null) {
                defaultDir = defaultDir.getParentDirectory();
            }

            result = defaultDir.findFile(PROGUARD_PRO);
        }

        return result;
    }
}

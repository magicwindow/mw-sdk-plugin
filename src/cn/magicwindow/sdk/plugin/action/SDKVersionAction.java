package cn.magicwindow.sdk.plugin.action;

import cn.magicwindow.sdk.plugin.PluginUtils;
import cn.magicwindow.sdk.plugin.Preconditions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.EverythingGlobalScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tony on 16/7/20.
 */
public class SDKVersionAction extends BaseAction {

    private final static String SDK_PATH = "com.zxinsight.MagicWindowSDK";
    private final static String SDK_VERSION_METHOD = "getSDKVersion";
    private final static String SDK_ROOT_PACKAGE = "com.zxinsight";
    private List<PsiClass> allClasses = new ArrayList<PsiClass>();

    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        PsiClass mSDK = PluginUtils.getClassForProject(project,SDK_PATH);

        if (mSDK == null) {
            PluginUtils.showErrorNotification(project,"请先下载sdk");
            return;
        }

        PsiMethod[] methods = mSDK.findMethodsByName(SDK_VERSION_METHOD,true);

        String message = "当前版本sdk版本:";
        if (methods!=null && methods[0]!=null) {
            PsiMethod method = methods[0];
            String version = getVersion(method.getText());
            if (Preconditions.isNotBlank(version)) {
                message += version;
            }
        }

        PsiPackage psiPackage = JavaPsiFacade.getInstance(project).findPackage(SDK_ROOT_PACKAGE);

        allClasses.clear();
        if (psiPackage!=null) {
            if (Preconditions.isNotBlank(psiPackage.getClasses())) {
                allClasses.addAll(Arrays.asList(psiPackage.getClasses()));
            }
            recursionPackage(psiPackage);
        }

        if (Preconditions.isNotBlank(allClasses)) {
            int methodCount = 0;
            for (PsiClass clazz:allClasses) {
                if (Preconditions.isNotBlank(clazz.getAllMethods())){
                    methodCount += clazz.getAllMethods().length;
                }
            }

            if (methodCount>0) {
                message += "\r\n" + "当前版本sdk方法数:"+methodCount;
            }
        }

        Messages.showMessageDialog(project,message,
                "MagicWindow SDK 信息",
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

    /**
     * 递归获取sdk的类
     * @param psiPackage
     */
    public void recursionPackage(PsiPackage psiPackage) {

        PsiPackage[] packages = psiPackage.getSubPackages();
        if (packages == null)
            return;

        for (PsiPackage item:packages) {
            if (item.getSubPackages()!=null) {
                if (Preconditions.isNotBlank(item.getClasses())) {
                    allClasses.addAll(Arrays.asList(item.getClasses()));
                }

                recursionPackage(item);
            }
        }
    }
}

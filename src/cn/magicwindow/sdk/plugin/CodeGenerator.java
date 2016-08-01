package cn.magicwindow.sdk.plugin;

import cn.magicwindow.sdk.plugin.exception.MWPluginException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;


/**
 * 魔窗sdk的代码生成器
 * Created by tony on 16/6/14.
 */
public class CodeGenerator {

    private PsiClass mClass;

    public CodeGenerator(PsiClass psiClass) {
        if (psiClass == null) {
            throw new MWPluginException("psiClass is null");
        }
        mClass = psiClass;
    }

    /**
     * 生成mLink配置
     */
    public void generateMLinkConfig() {

        PsiMethod onCreate = null;
        try {
            onCreate = mClass.findMethodsByName("onCreate", false)[0];
        } catch(ArrayIndexOutOfBoundsException e) {
            PluginUtils.showErrorNotification(mClass.getProject(), "MLink的配置只能在App的引导页或者首页的onCreate()中");
            return;
        }

        if (onCreate!=null) {
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(mClass.getProject());

            PsiStatement statementFromText = elementFactory.createStatementFromText(generateMLinkConfigCreator(), mClass);

            JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mClass.getProject());
            styleManager.shortenClassReferences(onCreate.getBody().add(statementFromText));
        }
    }

    private String generateMLinkConfigCreator() {
        StringBuilder sb = new StringBuilder();

        sb.append("if (com.zxinsight.MagicWindowSDK.getMLink() != null) {").append("\n");
        sb.append("  com.zxinsight.MagicWindowSDK.getMLink().registerWithAnnotation(this);").append("\n");
        sb.append("  android.net.Uri mLink = getIntent().getData();").append("\n");
        sb.append("  com.zxinsight.MagicWindowSDK.getMLink().router(mLink);").append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * 生成初始化魔窗sdk的配置
     * @param channel
     * @param ak
     */
    public void generateInitMWConfig(String channel,String ak) {

        PsiMethod onCreate = null;
        try {
            onCreate = mClass.findMethodsByName("onCreate", false)[0];
        } catch(ArrayIndexOutOfBoundsException e) {
            PluginUtils.showErrorNotification(mClass.getProject(), "SDK初始化配置只能在App的引导页、首页或者Application的onCreate()中");
            return;
        }

        if (onCreate!=null) {
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(mClass.getProject());

            PsiMethod methodFromText = elementFactory.createMethodFromText(generateInitMWConfigCreator(channel), mClass);
            PsiStatement statementFromText = elementFactory.createStatementFromText("initMW();",mClass);

            JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mClass.getProject());
            styleManager.shortenClassReferences(mClass.add(methodFromText));
            styleManager.shortenClassReferences(onCreate.getBody().add(statementFromText));
        }

        PsiFile manifest = PluginUtils.getAndroidManifest(mClass);
        if (manifest!=null) {
            VirtualFile childFile = manifest.getVirtualFile();
            Document document = FileDocumentManager.getInstance().getCachedDocument(childFile);
            if (document != null && document.isWritable()) {
                String androidManifest = document.getCharsSequence().toString();
                androidManifest = androidManifest.replace("</application>", generateAndroidManifest(channel,ak)+"\n\n </application>");
                Runnable writeRunnable = new WriteRunnable(androidManifest, document);
                ApplicationManager.getApplication().runWriteAction(writeRunnable);
            }
        } else {
            PluginUtils.showErrorNotification(mClass.getProject(),"找不到AndroidManifest文件");
        }

    }

    private String generateInitMWConfigCreator(String channel) {
        StringBuilder sb = new StringBuilder();

        sb.append("private void initMW() {").append("\n");
        sb.append("com.zxinsight.MWConfiguration config = new com.zxinsight.MWConfiguration(this);").append("\n");
        sb.append("config.setChannel(\""+channel+"\")").append("\n")
                .append(".setDebugModel(true)").append("\n")
                .append(".setPageTrackWithFragment(true)").append("\n")
                .append(".setWebViewBroadcastOpen(true)").append("\n")
                .append(".setSharePlatform(MWConfiguration.ORIGINAL)").append("\n")
                .append(".setMLinkOpen();").append("\n");
        sb.append("com.zxinsight.MagicWindowSDK.initSDK(config);").append("\n");
        sb.append("com.zxinsight.Session.setAutoSession(this);");
        sb.append("}");
        return sb.toString();
    }

    private String generateAndroidManifest(String channel,String ak) {

        StringBuilder sb = new StringBuilder();
        sb.append("<!--总的activity，必须注册！！！ -->");
        sb.append("\n");
        sb.append("<activity android:name=\"com.zxinsight.common.base.MWActivity\" android:exported=\"true\"\n" +
                "        android:configChanges=\"orientation|keyboardHidden|screenSize|navigation\"/>");
        sb.append("\n");
        sb.append("<!--MW sdk ID 此处跟activity同级，需要放在Application内，MW_APPID（也就是后台的” 魔窗AppKey”）不能更改 -->");
        sb.append("\n");
        sb.append("<meta-data android:name=\"MW_APPID\" android:value=\""+ak+"\" />");
        sb.append("\n");
        sb.append("<!--渠道名称MW_CHANNEL不能更改 -->");
        sb.append("\n");
        sb.append("<meta-data android:name=\"MW_CHANNEL\" android:value=\""+channel+"\" />");
        return sb.toString();
    }
}

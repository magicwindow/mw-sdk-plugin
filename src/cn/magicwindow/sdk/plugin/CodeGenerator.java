package cn.magicwindow.sdk.plugin;

import cn.magicwindow.sdk.plugin.exception.MWPluginException;
import cn.magicwindow.sdk.plugin.model.ActivityEntry;
import cn.magicwindow.sdk.plugin.model.AndroidManifest;
import cn.magicwindow.sdk.plugin.model.IntentCategory;
import cn.magicwindow.sdk.plugin.model.IntentFilterEntry;
import cn.magicwindow.sdk.plugin.xml.XmlHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 魔窗sdk的代码生成器
 * Created by tony on 16/6/14.
 */
public class CodeGenerator {

    private PsiClass mClass;
    private Project mProject;
    private List<ActivityEntry> activities = null;
    private static final String CHANNEL_PATTERN = "config.setChannel\\(\"" + "[0-9a-zA-Z]{0,}" + "\"\\)";
    private static final String MW_APPID_PATTERN = "<meta-data android:name=\"MW_APPID\" android:value=\"" + "[0-9a-zA-Z]{0,}" + "\" />";
    private static final String MW_CHANNEL_PATTERN = "<meta-data android:name=\"MW_CHANNEL\" android:value=\"" + "[0-9a-zA-Z]{0,}" + "\" />";

    public CodeGenerator(PsiClass psiClass) {
        if (psiClass == null) {
            throw new MWPluginException("psiClass is null");
        }

        mClass = psiClass;
        mProject = mClass.getProject();
    }

    /**
     * 生成当前类的mLink配置
     */
    public void generateMLinkConfig() {

        generateMLinkConfig(mClass);
    }

    /**
     * 根据某个类来生成mLink的配置
     *
     * @param psiClass
     */
    private void generateMLinkConfig(PsiClass psiClass) {

        PsiMethod onCreate = null;
        try {
            onCreate = psiClass.findMethodsByName("onCreate", false)[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            PluginUtils.showErrorNotification(mProject, "MLink的配置只能在App的引导页或者首页的onCreate()中");
            return;
        }

        if (onCreate != null) {
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(mProject);

            String mlinkConfig = generateMLinkConfigCreator(psiClass);
            if (Preconditions.isNotBlank(mlinkConfig)) {

                PsiStatement statementFromText = elementFactory.createStatementFromText(mlinkConfig, psiClass);
                JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
                styleManager.shortenClassReferences(onCreate.getBody().add(statementFromText));
            }
        }
    }

    private String generateMLinkConfigCreator(PsiClass psiClass) {

        PsiFile psiFile = psiClass.getContainingFile();
        StringBuilder sb = new StringBuilder();

        if (psiFile != null) {
            VirtualFile childFile = psiFile.getVirtualFile();
            Document document = FileDocumentManager.getInstance().getCachedDocument(childFile);
            if (document != null && document.isWritable()) {
                String content = document.getCharsSequence().toString();

                // 表示没有初始化过MLink
                if (content.indexOf("if (MagicWindowSDK.getMLink() != null) {") == -1 &&
                        content.indexOf("if (com.zxinsight.MagicWindowSDK.getMLink() != null) {") == -1
                        ) {

                    sb.append("if (com.zxinsight.MagicWindowSDK.getMLink() != null) {").append("\n");
                    sb.append("  com.zxinsight.MagicWindowSDK.getMLink().registerWithAnnotation(this);").append("\n");
                    sb.append("  android.net.Uri mLink = getIntent().getData();").append("\n");
                    sb.append("  com.zxinsight.MagicWindowSDK.getMLink().router(mLink);").append("\n");
                    sb.append("}");
                }
            }
        }

        return sb.toString();
    }

    /**
     * 生成初始化魔窗sdk的配置
     *
     * @param channel
     * @param ak
     */
    public void generateInitMWConfig(String channel, String ak) {

        generateInitMWConfigForApplication(mClass, channel);

        // 生成AndroidManifest.xml的配置
        PsiFile manifest = PluginUtils.getAndroidManifest(mClass);
        if (manifest != null) {
            VirtualFile childFile = manifest.getVirtualFile();
            Document document = FileDocumentManager.getInstance().getCachedDocument(childFile);
            if (document != null && document.isWritable()) {
                String androidManifest = document.getCharsSequence().toString();

                String content = generateAndroidManifest(document,androidManifest,channel, ak);

                if (Preconditions.isNotBlank(content)) {
                    androidManifest = androidManifest.replace("</application>", content + "\n\n </application>");
                    Runnable writeRunnable = new WriteRunnable(androidManifest, document);
                    ApplicationManager.getApplication().runWriteAction(writeRunnable);
                }
            }
        } else {
            PluginUtils.showErrorNotification(mProject, "找不到AndroidManifest.xml文件");
        }
    }

    private void generateInitMWConfigForApplication(PsiClass psiClass, String channel) {
        PsiMethod onCreate = null;
        try {
            onCreate = psiClass.findMethodsByName("onCreate", false)[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            PluginUtils.showErrorNotification(mProject, "SDK初始化配置只能在App的引导页、首页或者Application的onCreate()中");
            return;
        }

        if (onCreate != null) {
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(mProject);

            String initMWConfig = generateInitMWConfigCreator(psiClass, channel);

            if (Preconditions.isNotBlank(initMWConfig)) {
                PsiMethod methodFromText = elementFactory.createMethodFromText(initMWConfig, psiClass);
                PsiStatement statementFromText = elementFactory.createStatementFromText("initMW();", psiClass);

                JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
                styleManager.shortenClassReferences(psiClass.add(methodFromText));
                styleManager.shortenClassReferences(onCreate.getBody().add(statementFromText));
            }
        }
    }

    private String generateInitMWConfigCreator(PsiClass psiClass, String channel) {

        PsiFile psiFile = psiClass.getContainingFile();
        StringBuilder sb = new StringBuilder();

        if (psiFile != null) {
            VirtualFile childFile = psiFile.getVirtualFile();
            Document document = FileDocumentManager.getInstance().getCachedDocument(childFile);
            if (document != null && document.isWritable()) {
                String content = document.getCharsSequence().toString();

                // 表示没有初始化过魔窗的sdk
                if (content.indexOf("com.zxinsight.MWConfiguration config = new com.zxinsight.MWConfiguration(this);") == -1
                        && content.indexOf("MWConfiguration config = new MWConfiguration(this);") == -1) {
                    sb.append("private void initMW() {").append("\n");
                    sb.append("com.zxinsight.MWConfiguration config = new com.zxinsight.MWConfiguration(this);").append("\n");
                    sb.append("config.setChannel(\"" + channel + "\")").append("\n")
                            .append(".setDebugModel(true)").append("\n")
                            .append(".setPageTrackWithFragment(true)").append("\n")
                            .append(".setWebViewBroadcastOpen(true)").append("\n")
                            .append(".setSharePlatform(MWConfiguration.ORIGINAL)").append("\n")
                            .append(".setMLinkOpen();").append("\n");
                    sb.append("com.zxinsight.MagicWindowSDK.initSDK(config);").append("\n");
                    sb.append("com.zxinsight.Session.setAutoSession(this);");
                    sb.append("}");
                } else {
                    // 如果已经初始化过魔窗sdk,替换渠道
                    String newChannel = "config.setChannel(\"" + channel + "\")";
                    content = content.replaceFirst(CHANNEL_PATTERN, newChannel);
                    Runnable writeRunnable = new WriteRunnable(content, document);
                    ApplicationManager.getApplication().runWriteAction(writeRunnable);
                }
            }
        }

        return sb.toString();
    }

    private String generateAndroidManifest(Document document,String androidManifest,String channel, String ak) {

        StringBuilder sb = new StringBuilder();

        Pattern appPattern = Pattern.compile(MW_APPID_PATTERN);
        Matcher appMatcher = appPattern.matcher(androidManifest);

        Pattern channelPattern = Pattern.compile(MW_CHANNEL_PATTERN);
        Matcher channelMatcher = channelPattern.matcher(androidManifest);
        if (appMatcher.find() && channelMatcher.find()) {

            // 如果已经初始化过魔窗sdk,替换渠道
            String newAppID = "<meta-data android:name=\"MW_APPID\" android:value=\"" + ak + "\" />";
            androidManifest = androidManifest.replaceFirst(MW_APPID_PATTERN, newAppID);
            String newChannel = "<meta-data android:name=\"MW_CHANNEL\" android:value=\"" + channel + "\" />";
            androidManifest = androidManifest.replaceFirst(MW_CHANNEL_PATTERN, newChannel);
            Runnable writeRunnable = new WriteRunnable(androidManifest, document);
            ApplicationManager.getApplication().runWriteAction(writeRunnable);
        } else {
            sb.append("<!--总的activity，必须注册！！！ -->");
            sb.append("\n");
            sb.append("<activity android:name=\"com.zxinsight.common.base.MWActivity\" android:exported=\"true\"\n" +
                    "        android:configChanges=\"orientation|keyboardHidden|screenSize|navigation\"/>");
            sb.append("\n");
            sb.append("<!--MW sdk ID 此处跟activity同级，需要放在Application内，MW_APPID（也就是后台的” 魔窗AppKey”）不能更改 -->");
            sb.append("\n");
            sb.append("<meta-data android:name=\"MW_APPID\" android:value=\"" + ak + "\" />");
            sb.append("\n");
            sb.append("<!--渠道名称MW_CHANNEL不能更改 -->");
            sb.append("\n");
            sb.append("<meta-data android:name=\"MW_CHANNEL\" android:value=\"" + channel + "\" />");
        }

        return sb.toString();
    }

    public void generateAll(String channel, String ak) {

        PsiFile manifest = PluginUtils.getAndroidManifest(mClass);
        VirtualFile childFile = null;
        Document document = null;
        String manifestXmlString = null;
        if (manifest != null) {
            childFile = manifest.getVirtualFile();
            document = FileDocumentManager.getInstance().getCachedDocument(childFile);
            if (document != null && document.isWritable()) {
                manifestXmlString = document.getCharsSequence().toString();

                String content = generateAndroidManifest(document,manifestXmlString,channel, ak);

                if (Preconditions.isNotBlank(content)) {
                    manifestXmlString = manifestXmlString.replace("</application>", content + "\n\n </application>");
                    Runnable writeRunnable = new WriteRunnable(manifestXmlString, document);
                    ApplicationManager.getApplication().runWriteAction(writeRunnable);
                }
            }
        } else {
            PluginUtils.showErrorNotification(mProject, "找不到AndroidManifest.xml文件");
            return;
        }

        // 解析manifest文件
        if (manifestXmlString != null) {
            AndroidManifest androidManifest = new XmlHandler<AndroidManifest>().parse(AndroidManifest.class, manifestXmlString);
            if (androidManifest != null) {
                String packageName = androidManifest.getPackageName();
                ActivityEntry launcherActivity = null;

                if (androidManifest.getApplication() != null
                        && Preconditions.isNotBlank(androidManifest.getApplication().getActivities())) {
                    activities = androidManifest.getApplication().getActivities();

                    List<IntentFilterEntry> intentFilterEntryList = null;

                    List<ActivityEntry> removeList = new ArrayList<ActivityEntry>();
                    for (ActivityEntry activityEntry : activities) {
                        if (activityEntry.getName() != null && activityEntry.getName().startsWith(".")) {
                            activityEntry.setName(packageName + activityEntry.getName());
                        }

                        if (activityEntry.getName() != null && !activityEntry.getName().startsWith(packageName)) {
                            removeList.add(activityEntry);
                        }
                    }

                    // 删除一些使用第三方sdk的activity
                    if (Preconditions.isNotBlank(removeList)) {
                        activities.removeAll(removeList);
                    }

                    for (ActivityEntry activityEntry : activities) {
                        if (Preconditions.isNotBlank(activityEntry.getIntentFilter())) {
                            intentFilterEntryList = activityEntry.getIntentFilter();

                            for (IntentFilterEntry intentFilterEntry : intentFilterEntryList) {
                                if (Preconditions.isNotBlank(intentFilterEntry.getCategories())) {
                                    List<IntentCategory> intentCategories = intentFilterEntry.getCategories();
                                    for (IntentCategory category : intentCategories) {
                                        if ("android.intent.category.LAUNCHER".equals(category.getName())) {
                                            launcherActivity = activityEntry;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 获取启动的activity, 在启动的activity中注册mLink
                if (launcherActivity != null && Preconditions.isNotBlank(launcherActivity.getName())) {
                    PsiClass launcherClass = PluginUtils.getClassForProject(mProject, launcherActivity.getName());
                    if (launcherClass != null) {
                        generateMLinkConfig(launcherClass);
                    } else {
                        PluginUtils.showErrorNotification(mProject, "无法找到" + launcherActivity.getName());
                    }
                }

                // 获取application类, 在自定义的application中注册sdk
                if (androidManifest.getApplication() != null && Preconditions.isNotBlank(androidManifest.getApplication().getName())) {
                    if (androidManifest.getApplication().getName().startsWith(".")) {
                        androidManifest.getApplication().setName(packageName + androidManifest.getApplication().getName());
                    }

                    PsiClass applicationClass = PluginUtils.getClassForProject(mProject, androidManifest.getApplication().getName());
                    if (applicationClass != null) {
                        generateInitMWConfigForApplication(applicationClass, channel);
                    } else {
                        PluginUtils.showErrorNotification(mProject, "无法找到" + androidManifest.getApplication().getName());
                    }
                }
            }
        }
    }

    public List<ActivityEntry> getActivities() {
        return activities;
    }

    /**
     * 在activityies上生成MLink的注解
     *
     * @param activities
     */
    public void generateMLinkAnnotation(List<ActivityEntry> activities) {

        String activityName = null;
        String mlinkKey = null;
        PsiModifierList modifierList = null;
        PsiClass mLinkAnnotationClass = PluginUtils.getClassForProject(mProject, "com.zxinsight.mlink.annotation.MLinkRouter");
        PsiClass mLinkDefAnnotationClass = PluginUtils.getClassForProject(mProject, "com.zxinsight.mlink.annotation.MLinkDefaultRouter");
        PsiClass clazz = null;
        PsiJavaFile javaFile = null;
        if (mLinkAnnotationClass != null) {

            for (ActivityEntry activity : activities) {
                activityName = activity.getName();
                mlinkKey = activity.mlinkKey;
                clazz = PluginUtils.getClassForProject(mProject, activityName);
                if (clazz != null) {

                    javaFile = (PsiJavaFile) clazz.getContainingFile();
                    if (javaFile != null) {
                        if (Preconditions.isNotBlank(mlinkKey)) {
                            javaFile.importClass(mLinkAnnotationClass);
                            modifierList = clazz.getModifierList();
                            modifierList.addAnnotation("MLinkRouter(keys={\"" + mlinkKey + "\"})");
                        } else {
                            javaFile.importClass(mLinkDefAnnotationClass);
                            modifierList = clazz.getModifierList();
                            modifierList.addAnnotation("MLinkDefaultRouter");
                        }
                    }
                }
            }
        }
    }
}

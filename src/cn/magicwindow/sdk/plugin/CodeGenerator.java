package cn.magicwindow.sdk.plugin;

import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;


/**
 * 魔窗sdk的代码生成器
 * Created by tony on 16/6/14.
 */
public class CodeGenerator {

    private final PsiClass mClass;

    public CodeGenerator(PsiClass psiClass) {
        mClass = psiClass;
    }

    public void generateMLinkConfig() {
        PsiMethod onCreate = mClass.findMethodsByName("onCreate", false)[0];
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

    public void generateInitMWConfig(String channel) {
        PsiMethod onCreate = mClass.findMethodsByName("onCreate", false)[0];
        if (onCreate!=null) {
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(mClass.getProject());

            PsiMethod methodFromText = elementFactory.createMethodFromText(generateInitMWConfigCreator(channel), mClass);
            PsiStatement statementFromText = elementFactory.createStatementFromText("initMW();",mClass);

            JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mClass.getProject());
            styleManager.shortenClassReferences(mClass.add(methodFromText));
            styleManager.shortenClassReferences(onCreate.getBody().add(statementFromText));
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
}

package cn.magicwindow.sdk.plugin.action;

import cn.magicwindow.sdk.plugin.handler.GenerateMLinkHandler;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

import java.awt.event.KeyEvent;

/**
 * Created by tony on 16/7/23.
 */
public class GenerateMLinkDefaultRouterAction extends EditorAction {

    protected GenerateMLinkDefaultRouterAction() {
        super(new GenerateMLinkHandler(GenerateMLinkHandler.MLINK_DEFAULT_TYPE));
    }
}

package cn.magicwindow.sdk.plugin.action;


import cn.magicwindow.sdk.plugin.handler.GenerateMLinkHandler;
import com.intellij.openapi.editor.actionSystem.EditorAction;

/**
 * Created by tony on 16/7/23.
 */
public class GenerateMLinkRouterAction extends EditorAction {

    protected GenerateMLinkRouterAction() {
        super(new GenerateMLinkHandler(GenerateMLinkHandler.MLINK_TYPE));
    }
}

package cn.magicwindow.sdk.plugin.ui;

import cn.magicwindow.sdk.plugin.model.ActivityEntry;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.util.List;

/**
 * Created by tony on 16/8/5.
 */
public interface IConfirmListener {

    void onConfirm(Project project, Editor editor,List<ActivityEntry> activities);
}

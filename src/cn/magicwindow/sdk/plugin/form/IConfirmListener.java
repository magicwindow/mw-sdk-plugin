package cn.magicwindow.sdk.plugin.form;

import cn.magicwindow.sdk.plugin.model.ActivityEntry;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 16/8/5.
 */
public interface IConfirmListener {

    void onConfirm(Project project, Editor editor,List<ActivityEntry> activities);
}

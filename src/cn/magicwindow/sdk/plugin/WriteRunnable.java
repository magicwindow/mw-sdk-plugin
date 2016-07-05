package cn.magicwindow.sdk.plugin;

import com.intellij.openapi.editor.Document;

/**
 * Created by tony on 16/7/5.
 */
public class WriteRunnable implements Runnable {

    private String text;
    private Document document;

    WriteRunnable(String text, Document document) {
        this.text = text;
        this.document = document;
    }

    public void run() {
        document.setText(text);
    }
}

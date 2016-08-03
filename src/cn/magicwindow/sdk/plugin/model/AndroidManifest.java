package cn.magicwindow.sdk.plugin.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by tony on 16/8/3.
 */
@Root(name = "manifest", strict = false)
public class AndroidManifest {

    @Attribute(name = "package", required = true)
    private String packageName;

    @Element(name = "application")
    private ApplicationEntry application;

    private boolean isLauncher;

    public void setLauncher(boolean isLauncher) {
        this.isLauncher = isLauncher;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public ApplicationEntry getApplication() {
        return application;
    }

    public void setApplication(ApplicationEntry application) {
        this.application = application;
    }

}

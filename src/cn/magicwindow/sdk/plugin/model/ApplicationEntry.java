package cn.magicwindow.sdk.plugin.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by tony on 16/8/3.
 */
@Root(strict = false)
public class ApplicationEntry {

    @Attribute(name = "name", required = false)
    private String name;

    @ElementList(entry = "activity", inline = true)
    private List<ActivityEntry> activities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ActivityEntry> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityEntry> activities) {
        this.activities = activities;
    }
}

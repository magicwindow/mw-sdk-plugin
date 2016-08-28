package cn.magicwindow.sdk.plugin.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 16/8/28.
 */
public class EntryManager {

    private List<Entry> entries;
    private Entry selectedEntry;

    private EntryManager() {

        entries = new ArrayList<Entry>();
    }

    private static final EntryManager instance = new EntryManager();

    //静态工厂方法
    public static EntryManager getInstance() {
        return instance;
    }

    public void add(Entry entry) {
        entries.add(entry);
    }

    public void addAll(List<Entry> list) {
        entries.addAll(list);
    }

    public List<Entry> getAll() {
        return entries;
    }

    public Entry getSelectedEntry() {
        return selectedEntry;
    }

    public void setSelectedEntry(Entry selectedEntry) {
        this.selectedEntry = selectedEntry;
    }
}

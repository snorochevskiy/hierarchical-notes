package snorochevskiy.mynotes.markups;

import java.util.ArrayList;
import java.util.List;

public class MarkupsManager {

    private static final Object lock = new Object();

    private static volatile MarkupsManager instance = null;

    private final List<Markup> markups = new ArrayList<>();

    public static MarkupsManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MarkupsManager();

                    instance.initStandardMarkups();
                }
            }
        }

        return instance;
    }

    private MarkupsManager() {

    }

    private void initStandardMarkups() {
        markups.add(Markup.MARKDOWN);
    }

    public List<Markup> getMarkups() {
        return markups;
    }

    public Markup byName(String markupName) {
        for (Markup m : getMarkups()) {
            if (m.getName().equalsIgnoreCase(markupName)) {
                return m;
            }
        }

        return null;
    }
}

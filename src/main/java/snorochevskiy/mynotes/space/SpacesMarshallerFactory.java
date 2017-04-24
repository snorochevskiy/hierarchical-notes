package snorochevskiy.mynotes.space;

import java.util.HashMap;
import java.util.Map;

public class SpacesMarshallerFactory {

    private static final Object lock = new Object();

    private static volatile SpacesMarshallerFactory instance = null;

    private final Map<String, SpaceMarshaller> spaceMarshallers = new HashMap<>();

    public static SpacesMarshallerFactory getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new SpacesMarshallerFactory();
                    instance.initBuildInSpaceMarshallerFactories();
                }
            }
        }
        return instance;
    }

    private SpacesMarshallerFactory() {

    }

    private void initBuildInSpaceMarshallerFactories() {
        // TODO: use canonical name in v1.0
        spaceMarshallers.put(FsBasedSpace.class.getName(), new FsBasedSpaceMarshaller());
    }

    public SpaceMarshaller getMarshaller(String spaceClassName) {
        return spaceMarshallers.get(spaceClassName);
    }
}

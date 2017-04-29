package snorochevskiy.config;

import com.thoughtworks.xstream.XStream;
import snorochevskiy.mynotes.space.AbstractSpace;
import snorochevskiy.mynotes.space.SpaceMarshaller;
import snorochevskiy.mynotes.space.SpacesMarshallerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AppConfigManager {

    private static final String DIRECTORY_CONFIGS = "hierarchical-notes";
    private static final String DIRECTORY_SPACES = "spaces";

    private static final String FILE_APPLICATION_PROPERTIES = "application.properties";
    private static final String FILE_SPACES_XML = "spaces.xml";

    private static final String PROPERTY_LAST_ACTIVE_SPACE_ID = "lastActiveSpaceId";

    public static File getConfigDirectory() {
        File configDir = new File(System.getProperty("user.home") + File.separator + DIRECTORY_CONFIGS);
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        return configDir;
    }

    public static File getAppConfigPropertiesFile() {
        return  new File(getConfigDirectory(), FILE_APPLICATION_PROPERTIES);
    }

    public static File getSpacesXmlFile() {
        return  new File(getConfigDirectory(), FILE_SPACES_XML);
    }

    public static void write(AppConfig appConfig) {

        if (appConfig == null) {
            return;
        }

        try {
            Properties properties = new Properties();
            properties.setProperty(PROPERTY_LAST_ACTIVE_SPACE_ID, appConfig.getLastActiveSpaceId());
            // TODO : add version
            properties.store(new FileOutputStream(getAppConfigPropertiesFile()), null);

            XStream xstream = new XStream();
            xstream.toXML(appConfig.getOpenedSpaces(), new FileOutputStream(getSpacesXmlFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static AppConfig load() {

        File appConfigPropertiesFile = getAppConfigPropertiesFile();
        if (!appConfigPropertiesFile.exists()) {
            return null;
        }

        AppConfig appConfig = new AppConfig();
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(getAppConfigPropertiesFile()));
            appConfig.setLastActiveSpace(properties.getProperty(PROPERTY_LAST_ACTIVE_SPACE_ID));

            XStream xstream = new XStream();
            List<PersistedSpace> persistedSpaces = (List<PersistedSpace>)xstream.fromXML(new FileInputStream(getSpacesXmlFile()));
            appConfig.setOpenedSpaces(persistedSpaces);
            return appConfig;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<PersistedSpace> toPersistedSpace(List<AbstractSpace> spaces) {
        List<PersistedSpace> spacesToPersist = new ArrayList<>();
        for (AbstractSpace s : spaces) {
            PersistedSpace spaceToPersist = new PersistedSpace();
            SpaceMarshaller marshaller = SpacesMarshallerFactory.getInstance().getMarshaller(s.getClass().getName());
            spaceToPersist.setSpaceClassName(s.getClass().getName());
            spaceToPersist.setSpaceObject(marshaller.marshall(s));
            spacesToPersist.add(spaceToPersist);
        }
        return spacesToPersist;
    }
}

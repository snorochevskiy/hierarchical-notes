package snorochevskiy.config;

import java.util.List;

public class AppConfig {

    private List<PersistedSpace> openedSpaces;

    private String lastActiveSpaceId;

    public List<PersistedSpace> getOpenedSpaces() {
        return openedSpaces;
    }

    public void setOpenedSpaces(List<PersistedSpace> openedSpaces) {
        this.openedSpaces = openedSpaces;
    }

    public String getLastActiveSpaceId() {
        return lastActiveSpaceId;
    }

    public void setLastActiveSpace(String lastActiveSpaceId) {
        this.lastActiveSpaceId = lastActiveSpaceId;
    }
}

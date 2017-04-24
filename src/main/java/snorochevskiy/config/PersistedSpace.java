package snorochevskiy.config;

public class PersistedSpace {

    private String spaceClassName;
    private Object spaceObject;

    public String getSpaceClassName() {
        return spaceClassName;
    }

    public void setSpaceClassName(String spaceClassName) {
        this.spaceClassName = spaceClassName;
    }

    public Object getSpaceObject() {
        return spaceObject;
    }

    public void setSpaceObject(Object spaceObject) {
        this.spaceObject = spaceObject;
    }
}

package snorochevskiy.mynotes.space;

public interface SpaceMarshaller<SpaceType extends Space> {

    SpaceType unmarshall(Object obj);

    Object marshall(SpaceType space);
}

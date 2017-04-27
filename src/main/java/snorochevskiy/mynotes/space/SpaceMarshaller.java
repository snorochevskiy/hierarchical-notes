package snorochevskiy.mynotes.space;

public interface SpaceMarshaller<SpaceType extends AbstractSpace> {

    SpaceType unmarshall(Object obj);

    Object marshall(SpaceType space);
}

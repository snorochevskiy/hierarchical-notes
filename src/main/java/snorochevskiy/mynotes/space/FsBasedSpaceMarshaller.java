package snorochevskiy.mynotes.space;

import java.io.File;

public class FsBasedSpaceMarshaller implements SpaceMarshaller<FsBasedSpace> {
    @Override
    public FsBasedSpace unmarshall(Object obj) {
        return new FsBasedSpace(new File((String)obj));
    }

    @Override
    public Object marshall(FsBasedSpace space) {
        return space.getSpaceDirectory().getAbsolutePath();
    }
}

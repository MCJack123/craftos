import dan200.computercraft.api.filesystem.IMount;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ResourceMount implements IMount {



    @Override
    public boolean exists(String path) {
        return false;
    }

    @Override
    public boolean isDirectory(String path) {
        return false;
    }

    @Override
    public void list(String path, List<String> contents) {

    }

    @Override
    public long getSize(String path) {
        return 0;
    }

    @Override
    public InputStream openForRead(String path) {
        return null;
    }
}

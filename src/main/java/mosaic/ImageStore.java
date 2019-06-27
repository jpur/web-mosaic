package mosaic;

import java.io.File;
import java.io.IOException;

public interface ImageStore {
    String add(byte[] img) throws IOException;
    File get(String key) throws IOException;
}

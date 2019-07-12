package mosaic.data.store;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface ImageStore {
    String add(BufferedImage img, String format) throws IOException;
    File get(String key) throws IOException;
}

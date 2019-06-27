package mosaic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface ImageStore {
    String add(BufferedImage img) throws IOException;
    File get(String key) throws IOException;
}

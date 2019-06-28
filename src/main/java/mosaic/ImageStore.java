package mosaic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface ImageStore {
    File add(String key, String format, BufferedImage img) throws IOException;
    File get(String key) throws IOException;
}

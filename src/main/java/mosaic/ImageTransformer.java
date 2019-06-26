package mosaic;

import java.io.File;

public interface ImageTransformer {
    void transform(File file, byte[] image);
}

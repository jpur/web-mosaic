package mosaic;

import org.springframework.stereotype.Service;

import java.io.File;

//@Service
public class ImageTransformService {
    private final String rootDir;

    public ImageTransformService(String rootDir) {
        this.rootDir = rootDir;
    }

    public void transform(File file, byte[] image) {
    }
}

package mosaic.data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class FileSystemImageStore implements ImageStore {
    private final ConcurrentHashMap<String, File> images = new ConcurrentHashMap<>();
    private final String rootDir;

    public FileSystemImageStore(String rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public File add(String key, String format, BufferedImage img) throws IOException {
        File file = writeToFile(key, format, img);
        images.put(key, file);
        return file;
    }

    @Override
    public File get(String key) {
        return images.get(key);
    }

    private File writeToFile(String fileName, String format, BufferedImage img) throws IOException {
        File file = new File(rootDir, fileName);

        // TODO: This saves the wrong colour information for a small amount of JPG images with a different format. Need workaround.
        ImageIO.write(img, format, file);
        return file;
    }
}

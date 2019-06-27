package mosaic;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FileSystemImageStore implements ImageStore {
    static AtomicInteger nextAvailableId = new AtomicInteger(0);

    private final ConcurrentHashMap<String, File> images = new ConcurrentHashMap<>();

    @Override
    public String add(BufferedImage img) throws IOException {
        String id = getNextAvailableId();
        File file = writeToFile(id, img);
        images.put(id, file);
        return id;
    }

    @Override
    public File get(String key) {
        return images.get(key);
    }

    private String getNextAvailableId() {
        return Integer.toString(nextAvailableId.getAndIncrement());
    }

    private File writeToFile(String fileName, BufferedImage img) throws IOException {
        File file = ResourceUtils.getFile(String.format("src/main/user_images/%s", fileName));

        // TODO: This saves the wrong colour information for a small amount of JPG images with a different format. Need workaround.
        ImageIO.write(img, "jpg", file);
        return file;
    }
}

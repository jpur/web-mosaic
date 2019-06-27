package mosaic;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FileSystemImageStore implements ImageStore {
    static AtomicInteger nextAvailableId = new AtomicInteger(0);

    private ConcurrentHashMap<String, File> images = new ConcurrentHashMap<>();

    @Override
    public String add(byte[] img) throws IOException {
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

    private File writeToFile(String fileName, byte[] img) throws IOException {
        File file = ResourceUtils.getFile(String.format("src/main/user_images/%s.jpg", fileName));
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(img);
        fos.close();
        return file;
    }
}

package mosaic.data.store;

import mosaic.util.id.IdProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class FileSystemImageStore implements ImageStore {
    private final ConcurrentHashMap<String, File> images = new ConcurrentHashMap<>();

    private final String rootDir;
    private final IdProvider idProvider;

    public FileSystemImageStore(IdProvider provider, String rootDir) {
        this.idProvider = provider;
        this.rootDir = rootDir;

        // Track existing files TODO: Shouldn't be relying on this
        File[] files = new File(rootDir).listFiles();
        if (files != null) {
            for (File file : files) {
                images.put(file.getName(), file);
            }
        }
    }

    @Override
    public String add(BufferedImage img, String format) throws IOException {
        String key = idProvider.provide();
        String fileName = String.format("%s.%s", key, format);
        File file = writeToFile(fileName, format, img);
        images.put(key, file);
        return key;
    }

    @Override
    public File get(String key) {
        //File file = new File(key);
        //if (!file.exists()) throw new FileNotFoundException("File " + key + " not found.");

        return images.get(key);
    }

    private File writeToFile(String fileName, String format, BufferedImage img) throws IOException {
        File file = new File(rootDir, fileName);

        // Note: This sometimes outputs incorrect colors if writing to a JPG (long-term bug due to alpha values or something?)
        ImageIO.write(img, format, file);
        return file;
    }
}

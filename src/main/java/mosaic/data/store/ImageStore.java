package mosaic.data.store;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Used for interfacing with an underlying data store for retrieving and storing images
 */
public interface ImageStore {
    /**
     * Stores the given image of the given format type
     * @param img The image to store
     * @param format The image format
     * @return The key associated with the image to use for retrieval
     * @throws IOException Thrown if the image could not be stored
     */
    String add(BufferedImage img, String format) throws IOException;

    /**
     * Retrieves an image matching the given key
     * @param key The key to look up
     * @return The file of the image associated with the given key
     * @throws IOException Thrown if an image with the given key could not be found
     */
    File get(String key) throws IOException;
}

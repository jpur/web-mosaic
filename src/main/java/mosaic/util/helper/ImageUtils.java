package mosaic.util.helper;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class ImageUtils {
    private ImageUtils() {}

    public static BufferedImage resize(BufferedImage img, int newWidth, int newHeight, int hints) {
        Image tmp = img.getScaledInstance(newWidth, newHeight, hints);
        BufferedImage dimg = new BufferedImage(newWidth, newHeight, img.getType());

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static BufferedImage copy(BufferedImage img) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics g = out.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return out;
    }
}

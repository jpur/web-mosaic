package mosaic;

import mosaic.util.ColorCollection;
import mosaic.util.ColorOctree;
import mosaic.util.ColorUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static mosaic.util.ImageUtils.resize;

public final class MosaicData {
    private final int size;

    private ColorOctree<int[]> octree;

    public MosaicData(String rootDir, int size) {
        this.size = size;

        @SuppressWarnings("unchecked")
        List<int[]>[][][] images = (ArrayList<int[]>[][][])new ArrayList<?>[256][256][256];
        for (int i = 0; i <= 255; i++) {
            for (int j = 0; j <= 255; j++) {
                for (int k = 0; k <= 255; k++) {
                    images[i][j][k] = new ArrayList<>();
                }
            }
        }

        List<ColorCollection<int[]>> points = new ArrayList<>();
        File[] files = new File(rootDir).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        BufferedImage img = ImageIO.read(file);
                        img = resize(img, size, size, Image.SCALE_SMOOTH);
                        int[] rgb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
                        Color avgCol = ColorUtils.getAverageColor(rgb);

                        List<int[]> collection = images[avgCol.getRed()][avgCol.getGreen()][avgCol.getBlue()];
                        collection.add(rgb);

                        if (collection.size() == 1) {
                            points.add(new ColorCollection<>(avgCol, collection));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        octree = new ColorOctree<>(points);
    }

    public List<Color> getNearest(Color color, int k) {
        List<Color> nearest = new ArrayList<>();
        List<ColorCollection<int[]>> collections = octree.nearestNeighbor(color, k);

        nearest.add(collections.get(0).getKey());

        return nearest;
    }
}

package mosaic;

import mosaic.util.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import static mosaic.util.ImageUtils.resize;

public final class MosaicData {
    private final int size;

    private VectorOctree<Vector3i, List<int[]>> octree;

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

        List<SimpleEntry<Vector3i, List<int[]>>> points = new ArrayList<>();
        File[] files = new File(rootDir).listFiles();

        // TODO: Clean this up!
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
                            points.add(new SimpleEntry<>(new Vector3i(avgCol.getRed(), avgCol.getGreen(), avgCol.getBlue()), collection));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        octree = new VectorOctree<>(points);
    }

    public List<int[]> getNearest(Color color, int k) {
        List<SimpleEntry<Vector3i, List<int[]>>> collections = octree.nearestNeighbor(new Vector3i(color.getRed(), color.getGreen(), color.getBlue()), k);

        List<int[]> res = new ArrayList<>();
        for (SimpleEntry<Vector3i, List<int[]>> c : collections) {
            res.addAll(c.getValue());
        }

        return res;
    }
}

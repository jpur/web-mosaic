package mosaic;

import mosaic.util.ColorCollection;
import mosaic.util.ColorOctree;
import mosaic.util.ColorUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public final class MosaicData {
    private final int size;
    private List<int[]>[][][] images;

    private ColorOctree octree;

    public MosaicData(String rootDir, int size) {
        this.size = size;

        images = new ArrayList[256][256][256];
        for (int i = 0; i <= 255; i++) {
            for (int j = 0; j <= 255; j++) {
                for (int k = 0; k <= 255; k++) {
                    images[i][j][k] = new ArrayList<>();
                }
            }
        }

        List<ColorCollection> points = new ArrayList<>();

        File[] files = new File(rootDir).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                try {
                    BufferedImage img = ImageIO.read(file);
                    img = resize(img, size, size);
                    int[] rgb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
                    Color avgCol = ColorUtils.getAverageColor(rgb);

                    List<int[]> collection = images[avgCol.getRed()][avgCol.getGreen()][avgCol.getBlue()];
                    collection.add(rgb);

                    if (collection.size() == 1) {
                        points.add(new ColorCollection(avgCol, collection));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        octree = new ColorOctree(points);
    }

    public List<Color> getNearest(Color color, int k) {
        List<Color> nearest = new ArrayList<>();
        List<ColorCollection> collections = octree.nearestNeighbor(color, k);

        nearest.add(collections.get(0).getKey());

        return nearest;


        //return kNN(images, color, k);
    }

    public List<int[]> getImages(Color color) {
        return images[color.getRed()][color.getGreen()][color.getBlue()];
    }

    private static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    private List<Color> kNN(List<int[]>[][][] images, Color origin, int k) {
        List<Color> out = new ArrayList<>();

        Set<Color> colors = new HashSet<>();
        colors.add(origin);
        while (k > 0 && colors.size() > 0) {
            for (Color color : colors) {
                List<int[]> replacements = images[color.getRed()][color.getGreen()][color.getBlue()];
                if (replacements.size() > 0) {
                    out.add(color);
                    if (--k == 0) return out;
                }
            }

            Set<Color> newColors = new HashSet<>();
            getNeighbors(origin, colors, newColors);
            colors = newColors;
        }

        return out;
    }

    private void getNeighbors(Color origin, Set<Color> fromSet, Set<Color> neighborSet) {
        for (Color from : fromSet) {
            List<Color> neighbors = new ArrayList<>();

            if (from.getRed() < 255) neighbors.add(new Color(from.getRed() + 1, from.getGreen(), from.getBlue()));
            if (from.getRed() > 0) neighbors.add(new Color(from.getRed() - 1, from.getGreen(), from.getBlue()));

            if (from.getGreen() < 255) neighbors.add(new Color(from.getRed(), from.getGreen() + 1, from.getBlue()));
            if (from.getGreen() > 0) neighbors.add(new Color(from.getRed(), from.getGreen() - 1, from.getBlue()));

            if (from.getBlue() < 255) neighbors.add(new Color(from.getRed(), from.getGreen(), from.getBlue() + 1));
            if (from.getBlue() > 0) neighbors.add(new Color(from.getRed(), from.getGreen(), from.getBlue() - 1));

            for (Color n : neighbors) {
                if (ColorUtils.getDistance(n, origin) > ColorUtils.getDistance(from, origin)) {
                    neighborSet.add(n);
                }
            }
        }
    }
}

package mosaic.util;

import mosaic.data.MosaicImageInfo;
import java.awt.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

/**
 * A wrapper for a nearest-neighbor octree which stores image identifiers positioned by their color
 */
public final class MosaicMatcher {
    private VectorOctree<Vector3i, List<MosaicImageInfo>> octree;

    /**
     * Builds and initializes the internal octree with the given images
     * @param images The images the octree will hold
     */
    public MosaicMatcher(Collection<MosaicImageInfo> images) {
        Map<Vector3i, List<MosaicImageInfo>> colorMap = new HashMap<>();
        List<SimpleEntry<Vector3i, List<MosaicImageInfo>>> points = new ArrayList<>();

        // Sort each image into lists according to their positional value (average RGB color)
        for (MosaicImageInfo image : images) {
            Vector3i position = image.getPosition();

            if (!colorMap.containsKey(position)) {
                // First time seeing this color, create a new list
                List<MosaicImageInfo> list = new ArrayList<>();
                colorMap.put(position, list);
                points.add(new SimpleEntry<>(position, list));
            }

            // Add the image to its color list
            colorMap.get(position).add(image);
        }

        // Initialize the octree with a list of color,imageList pairs
        octree = new VectorOctree<>(points);
    }

    /**
     * Retrieves a set number of images nearest to the given color
     * @param color The color to search for
     * @param k The maximum number of images to return
     * @return A list of images closest to the given color
     */
    public List<MosaicImageInfo> getNearest(Color color, int k) {
        // Find the closest pairs
        List<SimpleEntry<Vector3i, List<MosaicImageInfo>>> collections = octree.nearestNeighbor(new Vector3i(color.getRed(), color.getGreen(), color.getBlue()), k);

        // Aggregate the pairs into a single list
        List<MosaicImageInfo> res = new ArrayList<>();
        for (SimpleEntry<Vector3i, List<MosaicImageInfo>> pair : collections) {
            res.addAll(pair.getValue());
        }

        return res;
    }
}

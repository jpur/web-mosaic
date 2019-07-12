package mosaic.util;

import mosaic.data.MosaicImageInfo;
import java.awt.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public final class MosaicMatcher {
    private VectorOctree<Vector3i, List<MosaicImageInfo>> octree;

    public MosaicMatcher(Collection<MosaicImageInfo> images) {
        Map<Vector3i, List<MosaicImageInfo>> colorMap = new HashMap<>();
        List<SimpleEntry<Vector3i, List<MosaicImageInfo>>> points = new ArrayList<>();

        for (MosaicImageInfo image : images) {
            Vector3i position = image.getPosition();

            if (!colorMap.containsKey(position)) {
                List<MosaicImageInfo> list = new ArrayList<>();
                colorMap.put(position, list);
                points.add(new SimpleEntry<>(position, list));
            }

            colorMap.get(position).add(image);
        }

        octree = new VectorOctree<>(points);
    }

    public List<MosaicImageInfo> getNearest(Color color, int k) {
        List<SimpleEntry<Vector3i, List<MosaicImageInfo>>> collections = octree.nearestNeighbor(new Vector3i(color.getRed(), color.getGreen(), color.getBlue()), k);

        List<MosaicImageInfo> res = new ArrayList<>();
        for (SimpleEntry<Vector3i, List<MosaicImageInfo>> pair : collections) {
            res.addAll(pair.getValue());
        }

        return res;
    }
}

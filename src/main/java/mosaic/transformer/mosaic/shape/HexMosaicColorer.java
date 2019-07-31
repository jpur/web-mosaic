package mosaic.transformer.mosaic.shape;

import mosaic.data.store.StoreClient;
import mosaic.util.MosaicMatcher;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class HexMosaicColorer extends MosaicShapeColorer {
    public HexMosaicColorer(MosaicMatcher matcher, StoreClient<int[]> mosaicStore, int size) {
        super(matcher, mosaicStore, size);
    }

    @Override
    public List<MosaicTask> getMosaicTasks(BufferedImage source, BufferedImage target, int maxTilesPerTask) {
        List<MosaicTask> tasks = new ArrayList<>();
        int numTiles = (int)(Math.ceil((double)target.getHeight() / size) * Math.ceil((double)target.getWidth() / size));
        for (int i = 0; i < numTiles; i += maxTilesPerTask) {
            tasks.add(new MosaicTask(source, target, i, Math.min(i + maxTilesPerTask, numTiles), 0, 0));
        }

        numTiles = (int)(Math.ceil((double)target.getHeight() / size + 1) * Math.ceil((double)target.getWidth() / size + 1));
        for (int i = 0; i < numTiles; i += maxTilesPerTask) {
            tasks.add(new MosaicTask(source, target, i, Math.min(i + maxTilesPerTask, numTiles), -size/2, -size/2));
        }

        return tasks;
    }

    @Override
    protected Polygon generatePolygon(int size) {
        int[] xPoints = { size/2, size, size, size/2, 0, 0 };
        int[] yPoints = { 0, size/4, 3*size/4, size, 3*size/4, size/4 };
        return new Polygon(xPoints, yPoints, 6);
    }
}

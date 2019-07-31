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

        // Create tasks for odd rows
        int numTiles = (int)(Math.ceil((double)target.getHeight() / size) * Math.ceil((double)target.getWidth() / size));
        for (int i = 0; i < numTiles; i += maxTilesPerTask) {
            // No offset for odd rows to fill gaps between two adjacent even tiles
            tasks.add(new MosaicTask(source, target, i, Math.min(i + maxTilesPerTask, numTiles), 0, 0));
        }

        // Create tasks for even rows (requires an extra row and column worth of tiles since we have a half-size offset)
        numTiles = (int)(Math.ceil((double)target.getHeight() / size + 1) * Math.ceil((double)target.getWidth() / size + 1));
        for (int i = 0; i < numTiles; i += maxTilesPerTask) {
            // Half-size offset for even rows to fill gaps between two adjacent odd tiles (requires an extra row and column)
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

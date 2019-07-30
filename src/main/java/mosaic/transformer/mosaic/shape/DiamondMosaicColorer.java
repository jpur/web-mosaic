package mosaic.transformer.mosaic.shape;

import mosaic.data.store.StoreClient;
import mosaic.util.MosaicMatcher;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DiamondMosaicColorer extends MosaicShapeColorer {
    public DiamondMosaicColorer(MosaicMatcher matcher, StoreClient<int[]> mosaicStore, int size) {
        super(matcher, mosaicStore, size);
    }

    @Override
    public List<MosaicTask> getMosaicTasks(BufferedImage source, BufferedImage target, int maxTilesPerTask) {
        List<MosaicTask> tasks = new ArrayList<>();
        int numTiles = (int)(Math.ceil((double)target.getHeight() / size) * Math.ceil((double)target.getWidth() / size));
        for (int i = 0; i < numTiles; i += maxTilesPerTask) {
            tasks.add(new MosaicTask(source, target, i, Math.min(i + maxTilesPerTask, numTiles), -size/2, -size/2));
        }

        numTiles = (int)(Math.ceil(((double)target.getHeight() - size/2) / size) * Math.ceil(((double)target.getWidth() - size/2) / size));
        for (int i = 0; i < numTiles; i += maxTilesPerTask) {
            tasks.add(new MosaicTask(source, target, i, Math.min(i + maxTilesPerTask, numTiles), 0, 0));
        }

        return tasks;
    }

    @Override
    protected Polygon generatePolygon(int size) {
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];
        xPoints[0] = size/2;
        xPoints[1] = size;
        xPoints[2] = size/2;
        xPoints[3] = 0;
        yPoints[0] = 0;
        yPoints[1] = size/2;
        yPoints[2] = size;
        yPoints[3] = size/2;

        return new Polygon(xPoints, yPoints, 4);
    }
}

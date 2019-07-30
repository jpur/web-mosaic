package mosaic.transformer.mosaic.shape;

import mosaic.data.store.StoreClient;
import mosaic.util.MosaicMatcher;

import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SquareMosaicColorer extends MosaicShapeColorer {
    public SquareMosaicColorer(MosaicMatcher matcher, StoreClient<int[]> mosaicStore, int size) {
        super(matcher, mosaicStore, size);
    }

    @Override
    public List<MosaicTask> getMosaicTasks(BufferedImage source, BufferedImage target, int maxTilesPerTask) {
        List<MosaicTask> tasks = new ArrayList<>();
        int numTiles = (int)(Math.ceil((double)target.getHeight() / size) * Math.ceil((double)target.getWidth() / size));
        System.out.println(numTiles);

        for (int i = 0; i < numTiles; i += maxTilesPerTask) {
            tasks.add(new MosaicTask(source, target, i, Math.min(i + maxTilesPerTask, numTiles)));
        }
        return tasks;
    }

    @Override
    protected Polygon generatePolygon(int size) {
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];
        xPoints[0] = 0;
        xPoints[1] = size;
        xPoints[2] = size;
        xPoints[3] = 0;
        yPoints[0] = 0;
        yPoints[1] = 0;
        yPoints[2] = size;
        yPoints[3] = size;

        return new Polygon(xPoints, yPoints, 4);
    }
}

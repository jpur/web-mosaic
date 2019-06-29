package mosaic.util;

import java.util.List;

public abstract class Octree<T> {
    public Octree(List<T> points) {
        build(points);
    }

    protected abstract void build(List<T> points);
}

package mosaic.util;

import java.awt.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

public class ColorCollection extends SimpleEntry<Color, List<int[]>> {
    public ColorCollection(Color key, List<int[]> value) {
        super(key, value);
    }

    public ColorCollection(Map.Entry<? extends Color, ? extends List<int[]>> entry) {
        super(entry);
    }
}

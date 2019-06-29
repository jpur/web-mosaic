package mosaic.util;

import java.awt.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

/**
 * A wrapper class for a color-collection pairing
 */
public class ColorCollection<T> extends SimpleEntry<Color, List<T>> {
    public ColorCollection(Color key, List<T> value) {
        super(key, value);
    }

    public ColorCollection(Map.Entry<? extends Color, ? extends List<T>> entry) {
        super(entry);
    }
}

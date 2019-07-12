package mosaic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import mosaic.util.Vector3i;

import java.awt.*;

public class MosaicImageInfo {
    private final String name;
    private final Color color;
    private final Vector3i position;

    public MosaicImageInfo(@JsonProperty("name") String name, @JsonProperty("color") Color color) {
        this.name = name;
        this.color = color;
        this.position = new Vector3i(color.getRed(), color.getGreen(), color.getBlue());
    }

    public String getName() {
        return name;
    }

    public Vector3i getPosition() {
        return position;
    }
}

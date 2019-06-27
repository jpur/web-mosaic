package mosaic;

import org.springframework.util.ResourceUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MosaicTransformer implements ImageTransformer {
    public enum Shape {
        Square,
        Hex,
    }

    private Shape shape;
    private int size;

    public MosaicTransformer(Shape shape, int size) {
        this.shape = shape;
        this.size = size;
    }

    @Override
    public byte[] transform(byte[] image) {
        byte[] out = new byte[image.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = 127;
        }
        return out;
    }
}

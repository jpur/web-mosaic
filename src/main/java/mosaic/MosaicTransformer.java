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
    public void transform(File file, byte[] image) {
        File f = null;
        try {
            f = ResourceUtils.getFile("src/main/user_images/0.jpg");
            System.out.println(f.getAbsolutePath());
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(image);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

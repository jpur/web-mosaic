package mosaic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static mosaic.FileSystemImageStore.nextAvailableId;

@Controller
public class MosaicTransformController {
    private final ImageStore imageStore;

    @Autowired
    public MosaicTransformController(ImageStore imageStore) {
        this.imageStore = imageStore;
    }

    @GetMapping("/transform")
    public String transform() {
        return "transform/index";
    }

    @PostMapping("/transform")
    public String transform(@RequestParam("image") MultipartFile image) throws IOException {
        ImageTransformer transformer = new MosaicTransformer(MosaicTransformer.Shape.Hex, 10);
        byte[] out = transformer.transform(image.getBytes());

        String key = imageStore.add(out);

        return String.format("redirect:/v/%s", key);
    }
}

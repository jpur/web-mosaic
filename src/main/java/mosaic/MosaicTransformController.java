package mosaic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class MosaicTransformController {
    private final AtomicInteger nextAvailableId = new AtomicInteger(0);
    private final ImageStore imageStore;

    @Autowired
    public MosaicTransformController(@Qualifier(value = "userStore") ImageStore imageStore) {
        this.imageStore = imageStore;
    }

    @GetMapping("/transform")
    public String transform() {
        return "transform/index";
    }

    @PostMapping("/transform")
    public String transform(@RequestParam("image") MultipartFile image) throws IOException {
        final String imageFormat = "jpg";

        ImageTransformer transformer = new MosaicTransformer(MosaicTransformer.Shape.Hex, 10);
        BufferedImage imgIn = ImageIO.read(image.getInputStream());
        BufferedImage imgOut = transformer.transform(imgIn);

        String key = String.format("%s.%s", getNextAvailableImageId(), imageFormat);
        imageStore.add(key, imageFormat, imgOut);

        return String.format("redirect:/v/%s", key);
    }

    private String getNextAvailableImageId() {
        return Integer.toString(nextAvailableId.getAndIncrement());
    }
}

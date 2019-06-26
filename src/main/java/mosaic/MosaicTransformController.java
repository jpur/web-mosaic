package mosaic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class MosaicTransformController {
    static AtomicInteger nextAvailableId = new AtomicInteger(0);

    @GetMapping("/transform")
    public String transform() {
        return "transform/index";
    }

    @PostMapping("/transform")
    public String transform(@RequestParam("image") MultipartFile image) throws IOException {

        ImageTransformer transformer = new MosaicTransformer(MosaicTransformer.Shape.Hex, 10);

        int id = nextAvailableId.getAndIncrement();
        File file = new File("/user_images", Integer.toString(id));
        transformer.transform(file, image.getBytes());

        return String.format("redirect:/v/%d", id);
    }
}

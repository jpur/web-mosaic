package mosaic.controller;

import mosaic.MosaicData;
import mosaic.data.ImageStore;
import mosaic.transformer.MosaicTransformer;
import mosaic.transformer.ThreadedMosaicTransformer;
import mosaic.util.id.IdProvider;
import mosaic.util.id.IncreasingIntegerIdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ExecutorServiceAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class MosaicTransformController {
    private final ImageStore imageStore;
    private final MosaicData mosaicData;
    private final MosaicTransformer transformer;

    private final TaskExecutor executor;

    private final String imageOutputFormat = "png";
    private final int tileSize = 10;

    @Autowired
    public MosaicTransformController(TaskExecutor executor,
                                     @Qualifier(value = "userStore") ImageStore imageStore,
                                     @Value("${mosaic.sub_img_path}") String subImgPath) {
        this.executor = executor;
        this.imageStore = imageStore;

        mosaicData = new MosaicData(subImgPath, tileSize);
        transformer = new ThreadedMosaicTransformer(new ExecutorServiceAdapter(executor), mosaicData, MosaicTransformer.Shape.Square, tileSize);
    }

    @GetMapping("/transform")
    public String transform() {
        return "transform/index";
    }

    @PostMapping("/transform")
    public String transform(@RequestParam("image") MultipartFile image) throws IOException {
        // Read in image
        InputStream in = image.getInputStream();
        BufferedImage imgIn = ImageIO.read(in);
        in.close();

        // Generate and save mosaic
        BufferedImage imgOut = transformer.transform(imgIn);
        String key = imageStore.add(imgOut, imageOutputFormat);

        // Redirect to mosaic location
        return String.format("redirect:/v/%s", key);
    }
}

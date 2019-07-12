package mosaic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import mosaic.data.ColorDeserializer;
import mosaic.data.store.ColorImageStoreClient;
import mosaic.util.MosaicMatcher;
import mosaic.data.MosaicImageInfo;
import mosaic.data.store.ImageStore;
import mosaic.transformer.MosaicTransformer;
import mosaic.transformer.ThreadedMosaicTransformer;
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
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Controller
public class MosaicTransformController {
    private final ImageStore userImageStore;
    private final MosaicMatcher mosaicData;
    private final MosaicTransformer transformer;

    private final TaskExecutor executor;

    private final String imageOutputFormat = "png";
    private final int tileSize = 10;

    @Autowired
    public MosaicTransformController(TaskExecutor executor,
                                     @Qualifier(value = "userStore") ImageStore userImageStore,
                                     @Qualifier(value = "imageStore") ImageStore subImageStore,
                                     @Value("${mosaic.sub_img_data_path}") String subImgDataPath) throws IOException {
        this.executor = executor;
        this.userImageStore = userImageStore;

        // Parse key/color pairs TODO: Shouldn't do this here, clean up later
        File file = new File(subImgDataPath);
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Color.class, new ColorDeserializer());
        mapper.registerModule(module);
        MosaicImageInfo[] info = mapper.readValue(file, MosaicImageInfo[].class);

        mosaicData = new MosaicMatcher(Arrays.asList(info));
        transformer = new ThreadedMosaicTransformer(new ExecutorServiceAdapter(executor),
                mosaicData, new ColorImageStoreClient(subImageStore), MosaicTransformer.Shape.Square, tileSize);
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
        String key = userImageStore.add(imgOut, imageOutputFormat);

        // Redirect to mosaic location
        return String.format("redirect:/v/%s", key);
    }
}

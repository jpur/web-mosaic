package mosaic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import mosaic.data.ColorDeserializer;
import mosaic.data.store.ColorImageStoreClient;
import mosaic.transformer.mosaic.*;
import mosaic.transformer.mosaic.shape.*;
import mosaic.util.MosaicMatcher;
import mosaic.data.MosaicImageInfo;
import mosaic.data.store.ImageStore;
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

    private final int tileSize;
    private final String imageOutputFormat;

    @Autowired
    public MosaicTransformController(TaskExecutor executor,
                                     @Qualifier(value = "userStore") ImageStore userImageStore,
                                     @Qualifier(value = "imageStore") ImageStore subImageStore,
                                     @Value("${mosaic.sub_img_data_path}") String subImgDataPath,
                                     @Value("${mosaic.sub_img_size}") int subImgSize,
                                     @Value("${mosaic.output_file_format}") String outputFileFormat) throws IOException {
        this.executor = executor;
        this.userImageStore = userImageStore;
        this.tileSize = subImgSize;
        this.imageOutputFormat = outputFileFormat;

        // Set up transformer
        MosaicImageInfo[] imageInfo = getKeyColorPairs(subImgDataPath);
        mosaicData = new MosaicMatcher(Arrays.asList(imageInfo));
        ShapeMosaicColorer shape = new SquareMosaicColorer(mosaicData, new ColorImageStoreClient(subImageStore), tileSize);
        transformer = new ThreadedMosaicTransformer(new ExecutorServiceAdapter(executor), shape);
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

    private MosaicImageInfo[] getKeyColorPairs(String subImgDataPath) throws IOException {
        // Parse key/image pairs from JSON data file
        File file = new File(subImgDataPath);
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Color.class, new ColorDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(file, MosaicImageInfo[].class);
    }
}

package mosaic;

import mosaic.data.store.FileSystemImageStore;
import mosaic.data.store.ImageStore;
import mosaic.data.store.S3ImageStore;
import mosaic.data.store.cache.InMemoryCache;
import mosaic.util.id.AlphanumericIdProvider;
import mosaic.util.id.IdProvider;
import mosaic.util.id.IncreasingIntegerIdProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

@Configuration
public class ApplicationWebConfiguration implements WebMvcConfigurer {
    @Value("${mosaic.store_service_type}")
    private String storeServiceType;

    @Value("${mosaic.user_img_path}")
    private String userImgPath;

    @Value("${mosaic.s3_bucket_name}")
    private String s3BucketName;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        try {
            String url = ResourceUtils.getURL(userImgPath).toString();
            registry.addResourceHandler("/user_images/**").addResourceLocations(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public IdProvider getImageStoreIdProvider() {
        return new AlphanumericIdProvider();
    }

    @Bean
    @Qualifier(value = "userStore")
    public ImageStore getUserStore(IdProvider idProvider, @Value("${mosaic.user_img_path}") String path) {
        ImageStore store;
        switch (storeServiceType.toLowerCase()) {
            case "s3":
                store = new S3ImageStore(idProvider, S3Client.builder().build(), s3BucketName, path);
                break;
            default:
                store = new FileSystemImageStore(idProvider, path);
                break;
        }

        return store;
    }

    @Bean
    @Qualifier(value = "imageStore")
    public ImageStore getImageStore(IdProvider idProvider, @Value("${mosaic.sub_img_path}") String path) {
        ImageStore store;
        switch (storeServiceType.toLowerCase()) {
            case "s3":
                store = new S3ImageStore(idProvider, S3Client.builder().build(), s3BucketName, path);
                break;
            default:
                store = new FileSystemImageStore(idProvider, path);
                break;
        }

        return store;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cores);
        executor.setMaxPoolSize(cores);
        return executor;
    }
}
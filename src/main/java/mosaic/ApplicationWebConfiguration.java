package mosaic;

import mosaic.data.FileSystemImageStore;
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
import java.io.IOException;

@Configuration
public class ApplicationWebConfiguration implements WebMvcConfigurer {
    @Value("${mosaic.user_img_path}")
    private String user_img_path;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        try {
            String url = ResourceUtils.getURL(user_img_path).toString();
            registry.addResourceHandler("/user_images/**").addResourceLocations(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public IdProvider getImageStoreIdProvider() {
        return new IncreasingIntegerIdProvider(0);
    }

    @Bean
    @Qualifier(value = "userStore")
    public FileSystemImageStore getUserStore(IdProvider idProvider, @Value("${mosaic.user_img_path}") String path) {
        return new FileSystemImageStore(idProvider, path);
    }

    @Bean
    @Qualifier(value = "imageStore")
    public FileSystemImageStore getImageStore(IdProvider idProvider, @Value("${mosaic.sub_img_path}") String path) {
        return new FileSystemImageStore(idProvider, path);
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
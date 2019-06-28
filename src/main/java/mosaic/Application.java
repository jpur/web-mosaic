package mosaic;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Qualifier(value = "userStore")
    public FileSystemImageStore getUserStore() {
        return new FileSystemImageStore("src/main/user_images");
    }

    @Bean
    @Qualifier(value = "imageStore")
    public FileSystemImageStore getImageStore() {
        return new FileSystemImageStore("src/main/resources/test");
    }
}

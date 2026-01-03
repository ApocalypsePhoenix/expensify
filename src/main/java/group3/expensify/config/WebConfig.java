package group3.expensify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration class to handle external resource mapping.
 * This allows images uploaded to the physical 'uploads' folder to be
 * served immediately by the web server without requiring a restart.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map the web URL path '/uploads/**' to the physical directory on the disk
        Path uploadDir = Paths.get("src/main/resources/static/uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // On Windows, the path needs to be prefixed with 'file:/'
        // On Linux/Mac, 'file:' is usually sufficient.
        String location = uploadPath.startsWith("/") ? "file:" + uploadPath + "/" : "file:/" + uploadPath + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
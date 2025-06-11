package com.sprint.mission.discodeit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * packageName    : com.sprint.mission.discodeit.config
 * fileName       : WebConfig
 * author         : doungukkim
 * date           : 2025. 5. 9.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 9.        doungukkim       최초 생성
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.all.path}")
    private String path;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath;

        uploadPath = new File(path).getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///" + uploadPath + "/");

    }
}

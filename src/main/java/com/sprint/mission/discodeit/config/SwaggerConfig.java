package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(title = "Discodeit API 문서",
        description = "Discodeit 프로젝트의 Swagger API 문서입니다."
    )
)
@Configuration
public class SwaggerConfig {

}
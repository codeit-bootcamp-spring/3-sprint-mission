package com.sprint.mission.discodeit;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "Discodeit API 문서",
        version = "v0",
        description = "Discodeit 프로젝트의 Swagger API 문서입니다."
    )
)
@Configuration
public class SwaggerConfig {

}

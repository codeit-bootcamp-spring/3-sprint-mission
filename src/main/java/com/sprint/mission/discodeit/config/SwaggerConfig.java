package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Discodeit API")
                .description("Discodeit 프로젝트의 Swagger API 문서입니다.")
                .version("v1.2")
                .contact(new Contact()
                    .name("조현아")
                    .email("akbkck8101@gmail.com")
                )
            )
            .servers(List.of(
                new Server().url("http://localhost:8080").description("로컬 서버")
            ));
    }
}

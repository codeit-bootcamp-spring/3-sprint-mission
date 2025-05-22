package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
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
            .version("v1.0.0")
            .contact(new Contact()
                .name("조현아")
                .email("akbkck8101@gmail.com")
            )
        )
        .servers(List.of(
            new Server().url("http://localhost:8080").description("로컬 서버")
        ))
        .tags(List.of(
            new Tag().name("Channel API").description("채널 관리"),
            new Tag().name("ReadStatus API").description("메시지 수신 정보 관리"),
            new Tag().name("Message API").description("메시지 관리"),
            new Tag().name("User API").description("사용자 관리"),
            new Tag().name("BinaryContent API").description("바이너리 파일 다운로드"),
            new Tag().name("Auth API").description("권한 관리")
        ));
  }
}

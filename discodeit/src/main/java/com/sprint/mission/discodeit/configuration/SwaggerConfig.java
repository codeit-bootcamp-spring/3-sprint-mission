package com.sprint.mission.discodeit.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI discodeitAPI() {
    return new OpenAPI()
        .components(new Components())
        .info(apiInfo());
  }

  private io.swagger.v3.oas.models.info.Info apiInfo() {
    return new io.swagger.v3.oas.models.info.Info()
        .title("Discoedit API API")
        .description("RESTful Discoedit API")
        .version("0.5.16");
  }


}

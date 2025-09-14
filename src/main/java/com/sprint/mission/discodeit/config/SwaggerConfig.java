package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * Swagger API 문서 생성을 위한 설정 클래스입니다.
 * 
 * <p>OpenAPI 3.0 스펙을 기반으로 API 문서를 구성하고,
 * 개발자가 API를 쉽게 이해하고 테스트할 수 있도록 도와줍니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>API 문서 제목 및 설명 설정</li>
 *   <li>서버 정보 구성</li>
 *   <li>Swagger UI를 통한 API 테스트 지원</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 문서를 구성합니다.
     * 
     * <p>API 문서의 기본 정보와 서버 설정을 포함한 
     * OpenAPI 객체를 생성합니다.</p>
     * 
     * @return 구성된 OpenAPI 객체
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Discodeit API 문서")
                .description("Discodeit 프로젝트의 Swagger API 문서입니다.")
            )
            .servers(List.of(
                new Server().url("http://localhost:8080").description("로컬 서버")
            ));
    }
}

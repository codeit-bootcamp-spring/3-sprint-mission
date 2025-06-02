package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(title = "이주용의 API 명세서",
        description = "스프린트 5 API 명세서입니다",
        version = "S5"
//        , contact = @Contact(
//        name = "문제점 보고하기",
//        email = "sjo06102@naver.com"
//    )
    )
)
public class SwaggerConfig {

}

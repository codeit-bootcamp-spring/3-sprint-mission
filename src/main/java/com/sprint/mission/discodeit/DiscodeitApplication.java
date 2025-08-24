package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Discodeit 애플리케이션의 메인 클래스입니다.
 * 
 * <p>Spring Boot 애플리케이션을 시작하고 JPA Auditing을 활성화합니다.</p>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }
}

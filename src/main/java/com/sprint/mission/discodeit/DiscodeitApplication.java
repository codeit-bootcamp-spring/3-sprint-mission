package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.sprint.mission.discodeit.controller",
    "com.sprint.mission.discodeit.service",
    "com.sprint.mission.discodeit.repository.jcf",
    "com.sprint.mission.discodeit.repository.file"
})
public class DiscodeitApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }
}
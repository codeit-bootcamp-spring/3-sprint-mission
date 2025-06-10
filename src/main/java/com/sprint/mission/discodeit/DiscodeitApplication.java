package com.sprint.mission.discodeit;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DiscodeitApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class,
        args);


  }
}

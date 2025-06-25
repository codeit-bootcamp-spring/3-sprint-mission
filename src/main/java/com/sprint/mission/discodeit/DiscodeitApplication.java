package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class DiscodeitApplication {

  public static void main(String[] args) {
    System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service Start🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️");
    SpringApplication.run(DiscodeitApplication.class, args);

  }

  // main메서드에는 DI 불가능하므로, 애플리케이션 실행 후 별도로 메서드 사용
  @EventListener(ApplicationReadyEvent.class)
  public void afterStartup() {
  }
}

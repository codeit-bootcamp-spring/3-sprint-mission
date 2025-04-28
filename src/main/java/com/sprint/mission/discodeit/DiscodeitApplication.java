package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.repository.factory.RepositoryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscodeitApplication {

  public static void main(String[] args) {
    SpringApplication.run(DiscodeitApplication.class, args);

    System.out.println("=== JCF 리포지토리 테스트 ===");
    TestInitializer.initializeAndTest(RepositoryFactory.createJCFRepositories());

    System.out.println("\n=== File 리포지토리 테스트 ===");
    TestInitializer.initializeAndTest(RepositoryFactory.createFileRepositories());
  }
}
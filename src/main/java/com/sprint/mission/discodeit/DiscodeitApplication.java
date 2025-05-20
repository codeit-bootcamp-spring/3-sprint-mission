package com.sprint.mission.discodeit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscodeitApplication implements CommandLineRunner {
    private final TestScenario testScenario;

    public DiscodeitApplication(TestScenario testScenario) {
        this.testScenario = testScenario;
    }

    @Override
    public void run(String... args) {
//        testScenario.run();
    }

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }
}
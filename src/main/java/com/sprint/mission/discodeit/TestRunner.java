package com.sprint.mission.discodeit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {
    private final TestLogic fileTestLogic;
    private final TestLogic basicTestLogic;

    public TestRunner(
            @Qualifier("fileTest") TestLogic fileTestLogic,
            @Qualifier("jfcTest")  TestLogic jfcTestLogic
    ) {
        this.fileTestLogic  = fileTestLogic;
        this.basicTestLogic = jfcTestLogic;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n------- BasicService & FileRepo Test -------");
        fileTestLogic.run();

        System.out.println("\n------- BasicService & JCFRepo Test -------");
        basicTestLogic.run();
    }
}
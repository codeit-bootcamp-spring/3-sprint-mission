package com.sprint.mission.discodeit.flow;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {
    private final TestScenario fileTestScenario;
    private final TestScenario jcfTestScenario;

    public TestRunner(
            @Qualifier("fileTest") TestScenario fileTestScenario,
            @Qualifier("jcfTest") TestScenario jcfTestScenario
    ) {
        this.fileTestScenario = fileTestScenario;
        this.jcfTestScenario = jcfTestScenario;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n------- BasicService & FileRepo Test -------");
        fileTestScenario.run();

        System.out.println("\n------- BasicService & JCFRepo Test -------");
        jcfTestScenario.run();
    }
}
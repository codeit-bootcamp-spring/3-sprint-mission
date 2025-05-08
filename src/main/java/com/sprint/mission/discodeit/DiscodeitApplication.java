package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.config.DiscodeitProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DiscodeitProperties.class)
public class DiscodeitApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscodeitApplication.class, args);
	}
}


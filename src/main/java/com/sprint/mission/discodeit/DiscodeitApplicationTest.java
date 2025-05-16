package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.ProfileImageCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class DiscodeitApplicationTest {

	public static void main(String[] args) {
		// Spring Context 생성
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplicationTest.class, args);
	}
}

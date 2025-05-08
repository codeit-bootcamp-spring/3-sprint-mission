package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entitiy.*;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

    }


}

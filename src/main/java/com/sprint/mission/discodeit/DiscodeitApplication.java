package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscodeitApplication {

  public static void main(String[] args) {
    SpringApplication.run(DiscodeitApplication.class, args);

    // 서비스 초기화 - JCF 리포지토리 사용
    System.out.println("=== JCF 리포지토리를 사용한 테스트 ===");
    UserRepository JCFUserRepository = new JCFUserRepository();
    ChannelRepository JCFChannelRepository = new JCFChannelRepository();
    MessageRepository JCFMessageRepository = new JCFMessageRepository();

    UserService userService = new BasicUserService(JCFUserRepository);
    ChannelService channelService = new BasicChannelService(JCFChannelRepository);
    MessageService messageService = new BasicMessageService(
        JCFMessageRepository,
        JCFUserRepository,
        JCFChannelRepository);

    // 셋업
    User user = setupUser(userService);
    Channel channel = setupChannel(channelService, user);
    // 테스트
    messageCreateTest(messageService, channel, user);

    // 서비스 초기화 - File 리포지토리 사용
    System.out.println("\n=== File 리포지토리를 사용한 테스트 ===");
    UserRepository fileUserRepository = FileUserRepository.createDefault();
    ChannelRepository fileChannelRepository = FileChannelRepository.createDefault();
    MessageRepository fileMessageRepository = FileMessageRepository.createDefault();

    UserService fileUserService = new BasicUserService(fileUserRepository);
    ChannelService fileChannelService = new BasicChannelService(
        fileChannelRepository);
    MessageService fileMessageService = new BasicMessageService(
        fileMessageRepository,
        fileUserRepository,
        fileChannelRepository);

    // 셋업
    User fileUser = setupUser(fileUserService);
    Channel fileChannel = setupChannel(fileChannelService, fileUser);
    // 테스트
    messageCreateTest(fileMessageService, fileChannel, fileUser);
  }


  private static User setupUser(UserService userService) {
    User user = userService.createUser("woody", "woody@codeit.com", "woody1234");
    return user;
  }

  private static Channel setupChannel(ChannelService channelService, User creator) {
    Channel channel = channelService.createChannel(creator, "공지");
    return channel;
  }

  private static void messageCreateTest(MessageService messageService, Channel channel,
      User author) {
    Message message = messageService.createMessage("안녕하세요.", author.getId(), channel.getId());
    System.out.println("메시지 생성: " + message.getId());
  }

}

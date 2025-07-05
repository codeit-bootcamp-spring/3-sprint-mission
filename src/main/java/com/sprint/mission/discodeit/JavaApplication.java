package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
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

public class JavaApplication {

  static User setupUser(UserService userService) {
    User user = userService.createUser("woody", "woody@codeit.com", "woody1234");
    return user;
  }

  static Channel setupChannel(ChannelService channelService, User creator) {
    Channel channel = channelService.createChannel(creator, "공지");
    return channel;
  }

  static void messageCreateTest(MessageService messageService, Channel channel, User author) {
    Message message = messageService.createMessage("안녕하세요.", author.getId(), channel.getId());
    System.out.println("메시지 생성: " + message.getId());
  }

  public static void main(String[] args) {
    // 서비스 초기화 - JCF 리포지토리 사용
    System.out.println("=== JCF 리포지토리를 사용한 테스트 ===");
    UserService userService = new BasicUserService(new JCFUserRepository());
    ChannelService channelService = new BasicChannelService(new JCFChannelRepository());
    MessageService messageService = new BasicMessageService(
        new JCFMessageRepository(),
        userService,
        channelService);

    // 셋업
    User user = setupUser(userService);
    Channel channel = setupChannel(channelService, user);
    // 테스트
    messageCreateTest(messageService, channel, user);

    // 서비스 초기화 - File 리포지토리 사용
    System.out.println("\n=== File 리포지토리를 사용한 테스트 ===");
    UserService fileUserService = new BasicUserService(FileUserRepository.createDefault());
    ChannelService fileChannelService = new BasicChannelService(
        FileChannelRepository.createDefault());
    MessageService fileMessageService = new BasicMessageService(
        FileMessageRepository.createDefault(),
        fileUserService,
        fileChannelService);

    // 셋업
    User fileUser = setupUser(fileUserService);
    Channel fileChannel = setupChannel(fileChannelService, fileUser);
    // 테스트
    messageCreateTest(fileMessageService, fileChannel, fileUser);
  }
}
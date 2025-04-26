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

    // JCF 리포지토리 테스트 실행
    System.out.println("=== JCF 리포지토리를 사용한 테스트 ===");
    initializeAndTest(new JCFUserRepository(), new JCFChannelRepository(),
        new JCFMessageRepository());

    // File 리포지토리 테스트 실행
    System.out.println("\n=== File 리포지토리를 사용한 테스트 ===");
    initializeAndTest(
        FileUserRepository.createDefault(),
        FileChannelRepository.createDefault(),
        FileMessageRepository.createDefault()
    );
  }

  /**
   * 리포지토리를 초기화하고 테스트 실행
   */
  private static void initializeAndTest(UserRepository userRepository,
      ChannelRepository channelRepository,
      MessageRepository messageRepository) {
    // 서비스 초기화
    UserService userService = new BasicUserService(userRepository);
    ChannelService channelService = new BasicChannelService(channelRepository);
    MessageService messageService = new BasicMessageService(messageRepository, userRepository,
        channelRepository);

    // 테스트 데이터 생성
    User user = setupUser(userService);
    Channel channel = setupChannel(channelService, user);

    // 메시지 생성 테스트
    messageCreateTest(messageService, channel, user);

    // 테스트 결과 출력
    printTestResults(userService, channelService, messageService);
  }

  /**
   * 사용자 설정
   */
  private static User setupUser(UserService userService) {
    return userService.createUser("woody", "woody@codeit.com", "woody1234");
  }

  /**
   * 채널 설정
   */
  private static Channel setupChannel(ChannelService channelService, User creator) {
    return channelService.createChannel(creator, "공지");
  }

  /**
   * 메시지 생성 테스트
   */
  private static void messageCreateTest(MessageService messageService, Channel channel,
      User author) {
    Message message = messageService.createMessage("안녕하세요.", author.getId(), channel.getId());
    System.out.println("메시지 생성 성공: " + message.getId());
  }

  /**
   * 테스트 완료 후 엔터티 전체 출력
   */
  private static void printTestResults(UserService userService, ChannelService channelService,
      MessageService messageService) {
    System.out.println("\n=== 테스트 결과 ===");

    System.out.println("모든 사용자:");
    userService.getAllUsers().forEach(System.out::println);

    System.out.println("\n모든 채널:");
    channelService.searchChannels(null, null).forEach(System.out::println);

    System.out.println("\n모든 메시지:");
    messageService.searchMessages(null, null, null).forEach(System.out::println);
  }
}
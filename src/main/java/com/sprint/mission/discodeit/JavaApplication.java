package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService.ParticipantAlreadyExistsException;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService.ParticipantNotFoundException;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService.MessageNotFoundException;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService.UserNotFoundException;
import com.sprint.mission.discodeit.service.jcf.JCFUserService.UserNotParticipantException;
import java.util.Optional;
import java.util.UUID;

public class JavaApplication {

  private UserService userService;
  private ChannelService channelService;
  private MessageService messageService;

  // 테스트에 사용할 객체들
  private User user1, user2, user3, user4, user5;
  private Channel channel1, channel2, channel3, channel4, channel5;
  private Message message1, message2, message3;

  public JavaApplication() {
    // 서비스 초기화
    this.userService = FileUserService.createDefault();
    this.channelService = FileChannelService.createDefault();
    this.messageService = FileMessageService.createDefault(userService, channelService);
  }

  public void runApplication() {
    System.out.println("[======= Discodeit 애플리케이션 실행 =======]");

    createUsers();
    createChannels();
    addParticipantsToChannels();
    createMessages();
    searchMessages();
    updateUserInfo();
    updateChannelInfo();
    updateMessageInfo();
    removeParticipant();
    validateScenarios();
    performDeletions();

    System.out.println("\n======= Discodeit 애플리케이션 종료 =======");
  }

  private void createUsers() {
    System.out.println("\n[============= 유저 생성 =============]");
    user1 = userService.createUser("user1@example.com", "홍길동", "password1");
    user2 = userService.createUser("user2@example.com", "김철수", "password2");
    user3 = userService.createUser("user3@example.com", "이영희", "password3");
    user4 = userService.createUser("user4@example.com", "박지성", "password4");
    user5 = userService.createUser("user5@example.com", "최민수", "password5");

    System.out.println(userService.getUserById(user1.getId()));
    System.out.println(userService.getUserByEmail(user2.getEmail()));
    System.out.println("등록된 전체 사용자 수: " + userService.getAllUsers().size());
  }

  private void createChannels() {
    System.out.println("\n[============= 채널 생성 =============]");
    channel1 = channelService.createChannel(user1, "개발자 모임");
    channel2 = channelService.createChannel(user1, "취미 공유방");
    channel3 = channelService.createChannel(user2, "주식 정보방");
    channel4 = channelService.createChannel(user2, "여행 계획");
    channel5 = channelService.createChannel(user3, "독서 토론방");

    System.out.println(channelService.getChannelById(channel1.getId()));
    System.out.println(channelService.getUserChannels(user1.getId()));
    System.out.println("사용자1의 채널 수: " + channelService.searchChannels(user1.getId(), null).size());
  }

  private void addParticipantsToChannels() {
    System.out.println("\n[============= 채널 참여자 추가 =============]");
    addParticipantWithLog(channel1, user2, "개발자 모임 채널에 user2 추가: ");
    addParticipantWithLog(channel1, user3, "개발자 모임 채널에 user3 추가: ");
    addParticipantWithLog(channel1, user4, "개발자 모임 채널에 user4 추가: ");
    addParticipantWithLog(channel2, user5, "취미 공유방 채널에 user5 추가: ");
    addParticipantWithLog(channel3, user1, "주식 정보방 채널에 user1 추가: ");

    System.out.println(channel1);
    Optional<Channel> channelOptional = channelService.getChannelById(channel1.getId());
    if (channelOptional.isPresent()) {
      System.out.println("개발자 모임 채널 참여자 수: " + channelOptional.get().getParticipants().size());
    } else {
      throw new ChannelNotFoundException(channel1.getId());
    }
  }

  private void addParticipantWithLog(Channel channel, User user, String description) {
    try {
      channelService.addParticipant(channel.getId(), user);
      System.out.println(description + ": 성공");
    } catch (ChannelNotFoundException e) {
      System.err.println(description + " 실패 - 채널 없음: " + e.getMessage());
    } catch (ParticipantAlreadyExistsException e) {
      System.err.println(description + " 실패 - 이미 참여 중: " + e.getMessage());
    }
  }

  private void createMessages() {
    System.out.println("\n[============= 메시지 생성 =============]");
    try {
      message1 = messageService.createMessage("안녕하세요! 개발자 모임에 오신 것을 환영합니다.", user1.getId(),
          channel1.getId());
      message2 = messageService.createMessage("반갑습니다.", user2.getId(), channel1.getId());
      message3 = messageService.createMessage("프로젝트 아이디어가 있으신가요?", user3.getId(), channel1.getId());

      System.out.println(message1);
      System.out.println(
          "개발자 모임 채널 메시지 수: " + messageService.getChannelMessages(channel1.getId()).size());
    } catch (UserNotFoundException | UserNotParticipantException | ChannelNotFoundException e) {
      System.err.println("오류: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("메시지 생성 중 오류 발생: " + e.getMessage());
      e.printStackTrace(); // 예외 스택 추적 출력
    }
  }

  private void searchMessages() {
    System.out.println("\n[============= 메시지 검색 =============]");
    System.out.println("\"환영\" 키워드가 포함된 메시지 검색:");
    messageService.searchMessages(null, null, "환영").forEach(System.out::println);

    System.out.println("\n채널1의 메시지 검색:");
    messageService.getChannelMessages(channel1.getId()).forEach(System.out::println);
  }

  private void updateUserInfo() {
    System.out.println("\n[============= 사용자 정보 업데이트 =============]");
    System.out.println("업데이트 전: " + user1.getName());
    userService.updateUser(user1.getId(), "홍길동(수정됨)", "newpassword1")
        .ifPresentOrElse(
            updatedUser -> System.out.println("업데이트 후: " + updatedUser.getName()),
            () -> System.out.println("사용자를 찾을 수 없습니다.")
        );
  }

  private void updateChannelInfo() {
    System.out.println("\n[============= 채널명 업데이트 =============]");
    System.out.println("업데이트 전: " + channel1.getName());
    Optional<Channel> updatedChannel = channelService.updateChannelName(channel1.getId(),
        "개발자 모임(수정됨)");
    if (updatedChannel.isPresent()) {
      System.out.println("업데이트 후: " + updatedChannel.get().getName());
    } else {
      throw new ChannelNotFoundException(channel1.getId());
    }
  }

  private void updateMessageInfo() {
    System.out.println("\n[============= 메시지 내용 업데이트 =============]");
    System.out.println("업데이트 전: " + message1.getContent());
    Optional<Message> updatedMessage = messageService.updateMessageContent(message1.getId(),
        "안녕하세요! 개발자 모임(수정됨)에 오신 것을 환영합니다.");
    if (updatedMessage.isPresent()) {
      System.out.println("업데이트 후: " + updatedMessage.get().getContent());

    } else {
      throw new MessageNotFoundException(message1.getId());
    }
  }

  private void removeParticipant() {
    System.out.println("\n[============= 채널 참여자 제거 =============]");

    try {
      // 참여자 제거 시도
      channelService.removeParticipant(channel1.getId(), user4.getId());
      System.out.println("개발자 모임 채널에서 user4 제거: 성공");

      // 제거 후 상태 확인
      channelService.getChannelById(channel1.getId())
          .ifPresentOrElse(
              channel -> System.out.println("제거 후 참여자 수: " + channel.getParticipants().size()),
              () -> {
                throw new ChannelNotFoundException(channel1.getId());
              }
          );

    } catch (ChannelNotFoundException | ParticipantNotFoundException e) {
      System.err.println("오류: " + e.getMessage());
    }
  }

  private void validateScenarios() {
    System.out.println("\n[============= 유효성 검사 =============]");
    System.out.println("시나리오 1: 채널에 참여하지 않은 사용자가 메시지 작성 시도");
    try {
      Message invalidMessage1 = messageService.createMessage("이 메시지는 작성될 수 없습니다.", user5.getId(),
          channel1.getId());
      System.out.println("결과: " + (invalidMessage1 == null ? "실패" : "성공"));
    } catch (JCFUserService.UserNotParticipantException e) {
      System.out.println("결과: 실패 - " + e.getMessage());
    } catch (Exception e) {
      System.out.println("결과: 예외 발생 - " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println("\n시나리오 2: 존재하지 않는 채널에 메시지 작성 시도");
    try {
      Message invalidMessage2 = messageService.createMessage("이 메시지도 작성될 수 없습니다.", user1.getId(),
          UUID.randomUUID());
      System.out.println("결과: " + (invalidMessage2 == null ? "실패" : "성공"));
    } catch (ChannelNotFoundException e) {
      System.out.println("결과: 실패 - " + e.getMessage());
    } catch (Exception e) {
      System.out.println("결과: 예외 발생 - " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void performDeletions() {
    System.out.println("\n[============= 삭제 작업 =============]");
    try {
      System.out.println("메시지 삭제: " + messageService.deleteMessage(message3.getId()));
      System.out.println(
          "삭제 후 채널1 메시지 수: " + messageService.getChannelMessages(channel1.getId()).size());
    } catch (MessageNotFoundException e) {
      System.err.println("메시지를 찾을 수 없습니다: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("메시지 삭제 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      System.out.println("\n채널 삭제: " + channelService.deleteChannel(channel5.getId()));
      System.out.println(
          "사용자3의 남은 채널 수: " + channelService.searchChannels(user3.getId(), null).size());
    } catch (ChannelNotFoundException e) {
      System.err.println("채널을 찾을 수 없습니다: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("채널 삭제 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      System.out.println("\n사용자 삭제: ");
      System.out.println("삭제된 사용자: " + userService.deleteUser(user4.getId()));
      System.out.println("삭제 후 전체 사용자 수: " + userService.getAllUsers().size());
    } catch (UserNotFoundException e) {
      System.err.println("사용자를 찾을 수 없습니다: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("사용자 삭제 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    JavaApplication app = new JavaApplication();
    app.runApplication();
  }
}
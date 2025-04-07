package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JcfChannelService;
import com.sprint.mission.discodeit.service.jcf.JcfMessageService;
import com.sprint.mission.discodeit.service.jcf.JcfUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JavaApplication {
  public static void main(String[] args) {
    //JcfUserServiceUsingMap userService = new JcfUserServiceUsingMap();
    JcfUserService userService = new JcfUserService();
    JcfChannelService channelService = new JcfChannelService(userService);
    JcfMessageService messageService = new JcfMessageService(userService, channelService);

    //  유저 생성
    User test01 = userService.createUser("test01", "test01@.com");
    User test02 = userService.createUser("test02", "test02@.com");
    User test03 = userService.createUser("test03", "test03@.com");
    User test04 = userService.createUser("test04", "test04@.com");

    //개별 유저 조회
    User testuser01 = userService.getUserById(test01.getId());
    System.out.println("===개별 User 조회 ===" + "\n" + testuser01);

    //전체 유저 조회
    System.out.println("\n=== 전체 유저 조회 ===");
    userService.getAllUsers().forEach(System.out::println);

    //유저 정보 수정 테스트
    System.out.println("\n=== 유저 정보(test01 이름변경) 수정 후 조회===");
    userService.updateUser(testuser01.getId(), "test0101", "test0101@.com");
    System.out.println(userService.getUserById(testuser01.getId()));

    //유저 삭제 후 전체 유저 조회 //  try-catch로 만약 유저가 존재하지 않으면 예외 처리 필요
    userService.deleteUser(test02.getId());
    System.out.println("\n=== 유저 삭제 후 전체 조회 ===");
    userService.getAllUsers().forEach(System.out::println);
    System.out.println();

    System.out.println("===============================================================================================");

    // test01과 test02이 채널 1개씩 생성
    Channel channel1 = channelService.createChannel("2025_Channel", test01);
    Channel channel2 = channelService.createChannel("2024_Channel", test02);

    // 개별 채널 조회
    Channel testChannel = channelService.getChannelById(channel2.getId());
    System.out.println("\n=== 개별 채널 조회 ===");
    System.out.println(testChannel);

    System.out.println("\n=== 채널 이름(2024_Channel) 수정 후 전체 채널 조회 ===");
    channelService.updateChannelName(channel2.getId(), "2023_channel");
    List<Channel> allChannels = channelService.getAllChannels();
    allChannels.forEach(System.out::println);

    // 유저 추가 (test02, test03을 채널에 추가)  & 삭제된 유저는 data에 담을 수 없다.
    System.out.println("\n=== 2025_Channel에 멤버(test02,test03) 추가 ===");
    channelService.addMember(channel1.getId(), test02.getId()); // User에서 test02를 삭제함.
    channelService.addMember(channel1.getId(), test03.getId());

    // 채널 멤버 조회
    System.out.println("\n=== 2025_Channel 전체멤버 조회 ===");
    List<User> channelMembers = channelService.getChannelMembers(channel1.getId());
    channelMembers.forEach(user -> System.out.println("멤버: " + user.getUsername()));

    // 유저 제거 테스트 (test03 제거)
    System.out.println("\n=== 2025_Channel에서 멤버 제거(test03) 후 조회 ===");
    channelService.removeMember(channel1.getId(), test03.getId());
    channelService.getChannelMembers(channel1.getId()).forEach(user -> System.out.println("멤버: " + user.getUsername()));

    // 채널 삭제 후 전체 채널 조회
    System.out.println("\n=== 2025_Channel 삭제 후 남은 채널 조회 ===");
    channelService.deleteChannel(channel1.getId());
    List<Channel> remainingChannels = channelService.getAllChannels();
    System.out.println(remainingChannels.isEmpty() ? "남은 채널 없음" : remainingChannels);

    System.out.println("\n===============================================================================================\n[##유저+채널+메시지 crud 테스트]\n");

    Map<String, User> users = new HashMap<>();
    String[][] userInfos = {
        {"john", "john@.com"},
        {"jane", "john@.com"}, // 중복
        {"tom", "tom@.com"},
        {"john", "bren@.com"},
        {"Ryan", "Ryan@.com"}
    };

    for (String[] info : userInfos) {
      try {
        User user = userService.createUser(info[0], info[1]);
        users.put(info[1], user);
      } catch (IllegalArgumentException e) {
        System.out.println("유저 생성 실패: " + e.getMessage());
      }
    }

    User user1 = users.get("john@.com");
    User user2 = users.get("Ryan@.com");
    User user3 = users.get("tom@.com");
    User user4 = users.get("bren@.com");

    Channel studyRoom = channelService.createChannel("공부방", user1);
    channelService.addMember(studyRoom.getId(), user2.getId());
    channelService.addMember(studyRoom.getId(), user3.getId());
    channelService.addMember(studyRoom.getId(), user4.getId());

    Channel noticeRoom = channelService.createChannel("공지방", user2);
    channelService.addMember(noticeRoom.getId(), user3.getId());
    channelService.addMember(noticeRoom.getId(), user4.getId());
    channelService.addMember(noticeRoom.getId(), user1.getId());

    messageService.createMessage(studyRoom.getId(), user1.getId(), "안녕하세요 저는 user1입니다.");
    messageService.createMessage(studyRoom.getId(), user4.getId(), "공부 열심히 하자!");

    messageService.createMessage(noticeRoom.getId(), user2.getId(), "공지사항입니다.");
    messageService.createMessage(noticeRoom.getId(), user1.getId(), "user1입니다.");
    messageService.createMessage(noticeRoom.getId(), user4.getId(), "user4입니다.");
    messageService.createMessage(noticeRoom.getId(), user3.getId(), "user3입니다.");

    try {
      Thread.sleep(1000); // 1000ms = 1초
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // === 메시지 기능 테스트 ===

    //공지방에서 user1이 보낸 메시지 조회
    System.out.println("\n[##공지방에서 user1이 보낸 메시지]");
    messageService.getMessagesBySenderInChannel(noticeRoom.getId(), user1.getId())
        .forEach(msg -> System.out.println(messageService.formatMessage(msg)));

    //공부방에서 user4가 받은 메시지 조회
    System.out.println("\n[##공부방에서 user4가 받은 메시지]");
    messageService.getMessagesByReceiverInChannel(studyRoom.getId(), user4.getId())
        .forEach(msg -> System.out.println(messageService.formatMessage(msg)));

    System.out.println("\n[##user3 탈퇴 처리]");
    userService.deleteUser(user3.getId());

    //공지방 전체 메시지 조회 (user3가 요청자라고 가정)
    System.out.println("\n[##공지방 전체 메시지 조회]");
    messageService.getAllMessagesInChannel(noticeRoom.getId(), user3.getId())
        .forEach(msg -> System.out.println(messageService.formatMessage(msg)));

    System.out.println("\n[##user1이 공부방에서 보낸 메시지 수정]");
    List<Message> messages = messageService.getMessagesBySenderInChannel(studyRoom.getId(), user1.getId());

    if (!messages.isEmpty()) {
      UUID messageId = messages.get(0).getId(); // 첫 번째 메시지를 대상으로 수정
      Message originalMessage = messageService.getMessageById(messageId); // 수정 전 메시지

      if (originalMessage != null) {
        String oldContent = originalMessage.getContent(); // 수정 전 내용

        messageService.updateMessage(messageId, user1.getId(), "안녕하세요 저는 user1입니다. 이름은 john입니다. (수정)");
        Message updatedMessage = messageService.getMessageById(messageId); // 수정 후 메시지

        System.out.println("메시지 수정 완료");
        System.out.println("수정 전: \"" + oldContent + "\"");
        System.out.println("수정 후: \"" + updatedMessage.getContent() + "\"");
      }
    } else {
      System.out.println("수정할 메시지가 없습니다.");
    }

    //공부방에서 user4가 보낸 메시지 삭제
    System.out.println("\n[##user4가 공부방에서 보낸 메시지 삭제]");
    List<Message> user4Msgs = messageService.getMessagesBySenderInChannel(studyRoom.getId(), user4.getId());
    if (!user4Msgs.isEmpty()) {
      UUID msgId = user4Msgs.get(0).getId();
      messageService.deleteMessage(msgId, user4.getId());
      System.out.println("삭제 완료");
    }

    // user3이 공부방에서 받은 메시지 조회
    System.out.println("\n[##user3이 공부방에서 받은 메시지 조회]");
    messageService.getMessagesByReceiverInChannel(studyRoom.getId(), user3.getId())
        .forEach(msg -> System.out.println(messageService.formatMessage(msg)));

    System.out.println("\n[##공지방 채널소유주인 user02가 탈퇴후 공지방 채널 메세지 조회]");
    //  예상 출력값: 존재하지 않는 사용자 && 존재하지 않는 채널
    //  도메인 간 양방향 의존성 문제 & 사용자 탈퇴 시 관련된 채널 및 연결 관계 정리 필요
  }
}

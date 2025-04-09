package com.sprint.mission.discodeit.run;

import java.time.LocalDateTime;
import java.util.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        //// 1) User 도메인
        // Create users (등록)
        userService.create("홍길동");
        userService.create("김민지");
        userService.create("차은우");
        userService.create("서강준");

        // Show all users (다건 조회)
        userService.readAll().forEach(System.out::println);
        System.out.println("=======================");

        // Show first user (단건 조회)
        System.out.println("첫번째 유저 검색");
        userService.read(userService.readAll().get(0).getId().toString()).forEach(System.out::println);
        System.out.println("=======================");

        // Search user by username (단건 조회)
        System.out.println("유저 이름으로 검색");
        String input =  new Scanner(System.in).nextLine();
        System.out.println("user: "+ input);
        userService.readByName(input).forEach(System.out::println);
        System.out.println("=======================");

        // Update user information (수정)
        System.out.println("첫번째 유저의 이름 변경");
        userService.update(userService.readAll().get(0).getId().toString(),"홍경");

        // Check updated user information (수정된 데이터 조회)
        userService.read(userService.readAll().get(0).getId().toString()).forEach(System.out::println);
        System.out.println("=======================");

        // Remove second user
        System.out.println("두번째 유저 삭제");
        userService.delete(userService.readAll().get(1).getId().toString());
        System.out.println("=======================");

        // Show all users after deletion
        System.out.println("삭제 후 유저 목록 확인");
        userService.readAll().forEach(System.out::println);
        System.out.println("=======================");

        // User-Channel 도메인 의존성 테스트
        System.out.println("로그인 하세요.");
        String username = new Scanner(System.in).nextLine();
        User user = userService.readByName(username).get(0);
        System.out.println(user.getName() + "(으)로 로그인되었습니다.");

        //// 2) Channel 도메인
        // Create channels (등록)
        channelService.create("코드잇_스프린트_SB_3기", user);
        channelService.create("SB_3기_4조", user);
        channelService.create("잡담방", user);

        // Show all channels (다건 조회)
        channelService.readAll().forEach(System.out::println);
        System.out.println("=======================");

        // Show first channel (단건 조회)
        System.out.println("첫번째 채팅방 검색");
        channelService.read(channelService.readAll().get(0).getId().toString())
                .forEach(System.out::println);
        System.out.println("=======================");

        // Search channel by channel name (단건 조회)
        System.out.println("채팅방 이름으로 검색");
        channelService.readByName("4조").forEach(System.out::println);
        System.out.println("=======================");

        // Update channel information (수정)
        System.out.println("첫번째 채팅방의 이름 변경");
        channelService.update(channelService.readAll().get(0).getId().toString(),"SB_3기_전체");

        // Check updated channel information (수정된 데이터 조회)
        channelService.read(channelService.readAll().get(0).getId().toString()).forEach(System.out::println);
        System.out.println("=======================");

        // Remove second channel
        System.out.println("마지막 채팅방 삭제");
        channelService.delete(channelService.readAll().get(channelService.readAll().size()-1).getId().toString());
        System.out.println("=======================");

        // Show all users after deletion
        System.out.println("삭제 후 채팅방 목록 확인");
        channelService.readAll().forEach(System.out::println);
        System.out.println("=======================");

        //// 3) Message 도메인
        // 채팅방 선택
        System.out.println("어느 채팅방에 입장하시겠습니까?");
        channelService.readAll();
        System.out.println("=======================");
        String channelname = new Scanner(System.in).nextLine();
        Channel channel = channelService.readByName(channelname).get(0);
        System.out.println(channel.getName() + " 입장!");

        System.out.println("------------------------");
        System.out.println(user.getName() + "님이 입장하셨습니다.");

        // Create messages (등록)
        messageService.create("안녕하세요~!", user, channel);
        // Show last message (단건 조회)
        messageService.read(messageService.readAll().get(messageService.readAll().size()-1).getId().toString()).forEach(System.out::println);

        messageService.create("^^", user, channel);
        // Show last message (단건 조회)
        messageService.read(messageService.readAll().get(messageService.readAll().size()-1).getId().toString()).forEach(System.out::println);

        messageService.create("잘 부탁드립니다!!!", user, channel);
        // Show last message (단건 조회)
        messageService.read(messageService.readAll().get(messageService.readAll().size()-1).getId().toString()).forEach(System.out::println);

        messageService.create("혹시 과제 다 하신 분??", user, channel);
        // Show last message (단건 조회)
        messageService.read(messageService.readAll().get(messageService.readAll().size()-1).getId().toString()).forEach(System.out::println);
        System.out.println("----------------------");
        System.out.println("여기까지 읽으셨습니다.");

        // Search message by text (단건 조회)
        System.out.println("메시지 검색");
        String searchWord = new Scanner(System.in).nextLine();
        messageService.readByText(searchWord).forEach(System.out::println);
        System.out.println("=======================");

        // Search message by sender name (단건 조회)
        System.out.println("보낸 사람 검색");
        messageService.readBySender(user.getId()).forEach(System.out::println);
        System.out.println("=======================");

        // Update message (수정)
        System.out.println("마지막 메시지 변경");
        messageService.update(messageService.readAll().get(messageService.readAll().size()-1).getId().toString(),"혹시 첫번째 과제 다 하신 분 계신가요??");

        // Check updated message (수정된 데이터 조회)
        messageService.read(messageService.readAll().get(messageService.readAll().size()-1).getId().toString()).forEach(System.out::println);
        System.out.println("=======================");

        messageService.create("저는 다 못 했어요ㅠㅠ", user, channel);

        // Show all messages (다건 조회)
        messageService.readAll().forEach(System.out::println);

        // Remove second message
        System.out.println("마지막 메시지 삭제");
        messageService.delete(messageService.readAll().get(messageService.readAll().size()-1).getId());
        System.out.println("=======================");

        // Show all users after deletion
        System.out.println("삭제 후 전체 메시지 확인");
        messageService.readAll().forEach(System.out::println);
        System.out.println("=======================");


//        // 채팅 프로그램 테스트
//        Scanner sc = new Scanner(System.in);
//        System.out.println("로그인 하세요.");
//        String name = sc.nextLine();
//        userService.create(name);
//        User user = userService.readByName(name).get(0);
//        System.out.println(name + "(으)로 로그인되었습니다.");


    }

}

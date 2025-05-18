package com.sprint.mission.discodeit.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.AuthService;

public class JavaApplication {
    public static void main(String[] args) throws IOException, IllegalAccessException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository);

        AuthService authService = new AuthService(userRepository);


        //// 1) User 도메인
        // Create users (등록)
        System.out.println("사용자 등록");
//        User user1 = userService.create("jspark", "jinsol8k@gmail.com","1234", "박진솔");
//        User user2 = userService.create("killdong", "hongkd@gmail.com","1234", "홍길동");

        // 다건 조회
        userService.findAll().forEach(System.out::println);

        // 단건 조회
        System.out.println("사용자 ID로 검색");
        System.out.println(userService.findByUsername("killdong").toString());
        System.out.println("-----------------------");

        // 단건 조회
        System.out.println("사용자 Email로 검색");
        System.out.println(userService.findByEmail("jinsol8k@gmail.com").toString());
        System.out.println("-----------------------");

        // 단건 조회
        System.out.println("사용자 이름으로 검색");
        userService.findByName("박진솔").forEach(System.out::println);
        System.out.println("-----------------------");

        System.out.println("로그인 하십시오.");
        User currentUser = authService.login("jspark", "123456");
        System.out.println("-----------------------");
        System.out.println("현재 사용자: ");
        System.out.println(userService.find(currentUser.getId()).toString());
        System.out.println("-----------------------");

        // 사용자 정보 수정
        System.out.println("비밀번호 변경");
        userService.updatePassword(currentUser.getId(), "123456@");
        System.out.println("-----------------------");

//        System.out.println("이전 비밀번호로 로그인 시도");
//        currentUser = authService.login("jspark", "1234");
//        System.out.println("-----------------------");

        System.out.println("변경 후 비밀번호로 로그인 시도");
        currentUser = authService.login("jspark", "123456@");
        System.out.println("-----------------------");

        // Delete user (삭제)
        System.out.println("삭제할 사용자를 입력해주십시오.");
        userService.delete(userService.findByUsername("killdong").getId());
        System.out.println("-----------------------");

        // 삭제 후 유저 목록 확인
        userService.findAll().forEach(System.out::println);
        System.out.println("-----------------------");

        System.out.println("=======================");

        //// 2. Channel 도메인
        // Create channels (등록)
        channelService.create(currentUser.getId(),"첫번째 채팅방");
        channelService.create(currentUser.getId(),"두번째 채팅방");

        // Show all channels (다건 조회)
        channelService.findAll().forEach(System.out::println);
        System.out.println("-----------------------");

        // Search channel by channel name (단건 조회)
        System.out.println("채팅방 이름으로 검색");
        channelService.findByName("첫번째").forEach(System.out::println);
        System.out.println("-----------------------");

        Channel currentChannel = channelService.findByName("두번째").get(0);

        // Update channel information (수정)
        System.out.println("변경할 채팅방 선택");
        channelService.updateName(currentChannel.getId(), "두번째 채팅방 -> 잡담방으로 변경함");
        System.out.println("-----------------------");

        // Check updated channel information (수정된 데이터 조회)
        channelService.findAll().forEach(System.out::println);

        // Remove channel (채팅방 삭제)
        System.out.println("삭제할 채팅방 선택");
        System.out.println("첫번째 채팅방 삭제");
        channelService.delete(channelService.findAll().get(0).getId());
        System.out.println("-----------------------");

        System.out.println("삭제 후 채팅방 목록 확인");
        channelService.findAll().forEach(System.out::println);
        System.out.println("-----------------------");

        System.out.println("=======================");

        //// 3) Message 도메인
        // Create messages (등록)
        System.out.println("(메시지 등록 종료: X)");
        System.out.println("-----------------------");
        messageService.create(currentUser.getId(), currentChannel.getId(), "하이요!");
        messageService.create(currentUser.getId(), currentChannel.getId(), "^^");
        messageService.create(currentUser.getId(), currentChannel.getId(), "반가워요~~~~~");
        messageService.create(currentUser.getId(), currentChannel.getId(), "좋은하루되세여!!!");
        messageService.create(currentUser.getId(), currentChannel.getId(), "ㅎㄱ ㄷㄱㄷㄱ");

        // Show all messages (다건 조회)
        System.out.println("메시지 전체 조회");
        messageService.findAll().forEach(System.out::println);
        System.out.println("-----------------------");

        // Search message by text (단건 조회)
        System.out.println("메시지 검색");
        messageService.findByText("ㄷㄱㄷㄱ").forEach(System.out::println);
        System.out.println("-----------------------");

        // Update message (수정)
        System.out.println("다섯번째 메시지 변경");
        messageService.update(messageService.findAll().get(4).getId(), "듀근듀근!!");

        // 변경된 메시지 확인
        System.out.println(messageService.find(messageService.findAll().get(4).getId()).toString());
        System.out.println("-----------------------");

        // Remove message (메시지 삭제)
        System.out.println("두번째 메시지 삭제");
        messageService.delete(messageService.findAll().get(1).getId());

        // 메시지 삭제 확인
        messageService.findAll().forEach(System.out::println);
        System.out.println("-----------------------");
    }

}


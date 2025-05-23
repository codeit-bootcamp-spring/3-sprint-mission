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

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        AuthService authService = context.getBean(AuthService.class);
        BasicUserStatusService userStatusService = context.getBean(BasicUserStatusService.class);
        BasicReadStatusService basicReadStatusService = context.getBean(BasicReadStatusService.class);

        Path path = Path.of("lena.jpg");
        byte[] imageBytes;

        try {
            imageBytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // ============== UserService 테스트 ==============
        System.out.println("============== Service 테스트 ==============");

        // 생성
        CreateUserRequest createUserRequest1 = new CreateUserRequest("asdf", "pass1", "user1@gmail.com");
        CreateUserRequest createUserRequest2 = new CreateUserRequest("zxcv", "pas","user2@gmail.com");
        CreateUserRequest createUserRequest3 = new CreateUserRequest("qwer", "pw12","user3@gmail.com");
        CreateBinaryContentRequest createBinaryContentRequest = new CreateBinaryContentRequest("image/jpeg", imageBytes);

        // 등록
        System.out.println("============== 등록 중복 테스트 ==============");
        User user = userService.create(createUserRequest1, Optional.of(createBinaryContentRequest));
        User user2 = userService.create(createUserRequest2, Optional.of(createBinaryContentRequest));
        User user3 = userService.create(createUserRequest3, Optional.empty());
        User duplicateUser = userService.create(createUserRequest1, Optional.empty());
        System.out.println();

        //조회(단건)
        System.out.println("============== 조회(단건) 테스트 ==============");
        FindUserRespond x = userService.find(user.getId());
        System.out.println(x.username()+"님의 email: "+x.email()+" 입니다.");
        System.out.println();

        //조회(다건)
        System.out.println("============== 조회(다건) 테스트 ==============");
        List<FindUserRespond> all = userService.findAll();
        for (FindUserRespond findUserRespond : all) {
            System.out.println(findUserRespond.username()+"님의 email: "+findUserRespond.email()+" 입니다.");
        }
        System.out.println();

        //수정
        System.out.println("============== 수정 테스트 ==============");
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(user.getId(),"updatedName","updatedPassword","qwer@naver.com",null);
        userService.update(updateUserRequest);

        //수정된 데이터 조회
        FindUserRespond find = userService.find(user.getId());
        System.out.println(find.username()+"님의 email: "+find.email()+" 입니다.");
        System.out.println();

        //삭제
        System.out.println("============== 삭제 테스트 ==============");
        userService.delete(user.getId());

        //조회를통해 삭제되었는지 확인
        List<FindUserRespond> deleteTest = userService.findAll();
        for (FindUserRespond findUserRespond : deleteTest) {
            System.out.println(findUserRespond.username()+"님의 email: "+findUserRespond.email()+" 입니다.");
        }
        System.out.println();

        // ============== ChannelService 테스트 ==============
        System.out.println("============== ChannelService 테스트 ==============");

        //등록
        List<User> users = new ArrayList<>();
        users.add(user2);
        users.add(user3);
        CreatePrivateChannelRequest createPrivateChannelRequest1 = new CreatePrivateChannelRequest(users);
        CreatePublicChannelRequest createPublicChannelRequest = new CreatePublicChannelRequest("채널 A", "de");
        Channel privateChannel = channelService.create(createPrivateChannelRequest1);
        Channel publicChannel = channelService.create(createPublicChannelRequest);

        //조회(단건)
        System.out.println("============== 조회(단건) 테스트 ==============");
        FindChannelRespond findChannelRespond = channelService.find(privateChannel.getId());
        System.out.println(findChannelRespond.channelType()+"채널 유저 UUID: "+findChannelRespond.privateChannelUsersId());
        findChannelRespond = channelService.find(publicChannel.getId());
        System.out.println(findChannelRespond.channelType()+"채널 이름: "+findChannelRespond.channelName()+", description:"+findChannelRespond.description());
        System.out.println();

        //조회(다건)
        System.out.println("============== 조회(다건) 테스트 ==============");
        List<FindChannelRespond> privateChannelFind = channelService.findAllByUserId(user2.getId());
        List<FindChannelRespond> publicChannelFind = channelService.findAllByUserId(new User().getId());
        //Private 채널에 속한 유저가 검색할때는 유저가 속한 private 채널도 표시
        System.out.println("privateChannel에 속한 유저");
        for (FindChannelRespond findChannelRespond1 : privateChannelFind) {
            System.out.println(findChannelRespond1);
        }
        System.out.println();

        //Private 채널에 가입하지 않은 유저는 public 채널만 표시
        System.out.println("publicChannel에 속한 유저");
        for (FindChannelRespond findChannelRespond1 : publicChannelFind) {
            System.out.println(findChannelRespond1);
        }
        System.out.println();

        //수정
        System.out.println("============== 수정 테스트 ==============");
        UpdateChannelRequest updateChannelRequest = new UpdateChannelRequest(publicChannel.getId(),"updatedName","updatedDescription");
        channelService.update(updateChannelRequest);
        //수정된 데이터 조회
        privateChannelFind = channelService.findAllByUserId(user2.getId());
        for (FindChannelRespond findChannelRespond1 : privateChannelFind) {
            System.out.println(findChannelRespond1);
        }
        System.out.println();

        //삭제
        System.out.println("============== 삭제 테스트 ==============");
        channelService.delete(publicChannel.getId());

        //조회를통해 삭제되었는지 확인
        privateChannelFind = channelService.findAllByUserId(user2.getId());
        for (FindChannelRespond findChannelRespond1 : privateChannelFind) {
            System.out.println(findChannelRespond1);
        }
        System.out.println();

        // ============== MessageService 테스트 ==============
        System.out.println("============== MessageService 테스트 ==============");

        //등록(1개는 이미지, 1개는 텍스트
        FindUserRespond findUserRespond = userService.find(user2.getId());
        CreateBinaryContentRequest createBinaryContentRequest1 = new CreateBinaryContentRequest("image/jpeg", imageBytes);
        List<CreateBinaryContentRequest> createBinaryContentRequests = new ArrayList<>();
        createBinaryContentRequests.add(createBinaryContentRequest1);
        CreateMessageRequest createMessageRequest1 = new CreateMessageRequest(privateChannel.getId(),user2.getId(),"테스트 이미지",Optional.of(createBinaryContentRequests));
        CreateMessageRequest createMessageRequest2 = new CreateMessageRequest(privateChannel.getId(),user3.getId(),"테스트 텍스트",Optional.empty());
        messageService.create(createMessageRequest1);
        messageService.create(createMessageRequest2);

        //조회
        System.out.println("============== 채널 id로 조회 테스트 ==============");
        List<Message> messageByChannelId = messageService.findAllByChannelId(privateChannel.getId());
        messageByChannelId.forEach(System.out::println);
        System.out.println();

        //수정
        System.out.println("============== 수정 테스트 ==============");
        Message message1 = messageByChannelId.get(1);
        UpdateMessageRequest updateMessageRequest = new UpdateMessageRequest(message1.getId(), "테스트 텍스트 수정", Optional.empty());
        messageService.update(updateMessageRequest);

        //수정된 데이터 조회
        messageService.findAllByChannelId(privateChannel.getId()).forEach(System.out::println);
        System.out.println();

        //삭제
        System.out.println("============== 삭제 테스트 ==============");
        messageService.delete(message1.getId());

        //조회를통해 삭제되었는지 확인
        messageService.findAllByChannelId(privateChannel.getId()).forEach(System.out::println);
        System.out.println();


        // ============== AuthService 테스트 ==============
        System.out.println("============== AuthService 테스트 ==============");

        //로그인 (기존에 만들어둔 Id)
        LoginRequest loginRequest = new LoginRequest("zxcv", "pas");
        User login = authService.login(loginRequest);
        System.out.println(login+" 로그인성공");

        //로그인 (존재하지않는 Id (오류발생))
        LoginRequest loginRequest2 = new LoginRequest("qwea", "pas");
        authService.login(loginRequest2);
        System.out.println();

        // ============== UserStatusService 테스트 ==============
        System.out.println("============== UserStatusService 테스트 ==============");

        //생성 (중복일경우)
        System.out.println("============== 중복 테스트 ==============");
        userStatusService.create(new CreateUserStatusRequest(user2.getId()));
        System.out.println();

        //조회(단일)
        System.out.println("============== 단일 조회 테스트 ==============");
        UserStatus byUserId = userStatusService.findByUserId(user2.getId());
        System.out.println(byUserId);
        System.out.println();


        //조회(다수)
        System.out.println("============== 다수 조회 테스트 ==============");
        userStatusService.findAll().forEach(System.out::println);
        System.out.println();

        //수정
        System.out.println("============== 온라인 테스트 ==============");
        userStatusService.updateByUserId(new UpdateUserStatusRequest(byUserId.getId(),user2.getId()));
        UserStatus userStatus = userStatusService.find(byUserId.getId());
        System.out.println(userStatus.IsOnline());
        System.out.println();

        //삭제
        System.out.println("============== 삭제 테스트 ==============");
        userStatusService.delete(byUserId.getId());
        userStatusService.findAll().forEach(System.out::println);
        System.out.println();

        //생성
        System.out.println("============== 생성 테스트 ==============");
        userStatusService.create(new CreateUserStatusRequest(user2.getId()));
        userStatusService.findAll().forEach(System.out::println);
        System.out.println();


        // ============== ReadStatusService 테스트 ==============
        System.out.println("============== ReadStatusService 테스트 ==============");

        //생성 (중복일경우)
        System.out.println("============== 중복 테스트 ==============");
        basicReadStatusService.create(new CreateReadStatusRequest(user2.getId(), privateChannel.getId()));
        System.out.println();

        //조회
        System.out.println("============== 조회 테스트 ==============");
        basicReadStatusService.findAllByUserId(user2.getId()).forEach(System.out::println);
        System.out.println();

        //수정
        System.out.println("============== 마지막으로읽은시간 update 테스트 ==============");
        ReadStatus readStatus = basicReadStatusService.findAllByUserId(user2.getId()).get(0);
        basicReadStatusService.update(new UpdateReadStatusRequest(readStatus.getId(), user2.getId(), privateChannel.getId()));
        System.out.println(basicReadStatusService.findAllByUserId(user2.getId()).get(0).getUpdatedAt());
        System.out.println();

        //삭제
        System.out.println("============== 삭제 테스트 ==============");
        basicReadStatusService.delete(readStatus.getId());
        basicReadStatusService.findAllByUserId(user2.getId()).forEach(System.out::println);
        System.out.println();

        //생성
        System.out.println("============== 생성 테스트 ==============");
        basicReadStatusService.create(new CreateReadStatusRequest(user2.getId(), privateChannel.getId()));
        basicReadStatusService.findAllByUserId(user2.getId()).forEach(System.out::println);
        System.out.println();


    }


}

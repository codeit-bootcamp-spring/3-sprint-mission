package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    private static User userCat;
    private static User userDog;
    private static Channel channel;
    private static Message message;

    static User setupUser(UserService userService) {
//        user = new User("냥냥이", "woody@codeit.com", "woody1234");

        // 프로필사진 있는  유저 생성
        UserCreateResponse 댕댕이CreateResponseWithProfile = userService.create(new UserCreateRequest("댕댕이", "woody@codeit.com", "woody1234", UUID.randomUUID()));
        userDog = 댕댕이CreateResponseWithProfile.user();
        System.out.println("----------프로필있는 유저 생성----------");
        System.out.println(댕댕이CreateResponseWithProfile);

        // 프로필사진 없는 유저 생성
        UserCreateResponse 냥냥이CreateResponse = userService.create(new UserCreateRequest("냥냥이", "woody@codeit.com", "woody1234", null));
        userCat = 냥냥이CreateResponse.user();
        System.out.println("----------프로필없는 유저 생성----------");
        System.out.println(냥냥이CreateResponse);


        // 테스트
        System.out.println("---------- 유저 findById ----------");
        System.out.println(userService.find(냥냥이CreateResponse.user().getId()).toString());
        System.out.println("---------- 유저 findAll ----------");
        for (UserResponse userRes : userService.findAll()) {
            System.out.println("name : " + userRes.name());
        }
        System.out.println("---------- 유저 update ----------");
        UserCreateResponse updatedUserResponse = userService.update(new UserUpdateRequest(댕댕이CreateResponseWithProfile.user().getId(), "댕댕이 진화함", null, null, null));
        System.out.println(updatedUserResponse.user().getName() + " 로 업데이트 됨");

//        System.out.println("---------- 유저 delete 후 결과 조회 ----------");
//        userService.delete(userCreateResponseWithProfile.user().getId());
        System.out.println("---------- 유저 findAll ----------");
        for (UserResponse userRes : userService.findAll()) {
            System.out.println("name : " + userRes.name());
        }

        return 댕댕이CreateResponseWithProfile.user();
    }

    static Channel setupChannel(ChannelService channelService) {
//        channel = new Channel("공지", ChannelType.PUBLIC, "공개 채널입니다.", user.getId());

        // 공개 채널 생성
        ChannelCreateResponse 댕댕공개채널_CreateResponse = channelService.create(new PublicChannelCreateRequest("댕댕이들의 공개 채널", ChannelType.PUBLIC, "댕댕 공지사항", userDog.getId()));
        ChannelCreateResponse 댕댕공개채널2_CreateResponse = channelService.create(new PublicChannelCreateRequest("댕댕이들의 공개 채널2", ChannelType.PUBLIC, "댕댕 공지사항2", userDog.getId()));
        System.out.println("----------공개 채널 생성----------");
        System.out.println(댕댕공개채널_CreateResponse.toString());
        System.out.println(댕댕공개채널2_CreateResponse.toString());

        // 비공개 채널 생성
        ChannelCreateResponse 냥냥비공개채널_CreateResponse = channelService.create(new PrivateChannelCreateRequest(ChannelType.PRIVATE, userCat.getId()));
        ChannelCreateResponse 댕댕비공개채널_CreateResponse = channelService.create(new PrivateChannelCreateRequest(ChannelType.PRIVATE, userDog.getId()));
        System.out.println("----------비공개 채널 생성----------");
        System.out.println(냥냥비공개채널_CreateResponse.toString());
        System.out.println(댕댕비공개채널_CreateResponse.toString());

        // 테스트
        System.out.println("---------- 채널 findById ----------");
        System.out.println(channelService.find(댕댕공개채널_CreateResponse.channel().getId()).toString());
        System.out.println("---------- 채널 findAllByUserId (댕댕이들) ----------");
        for (ChannelResponse chanRes : channelService.findAllByUserId(userDog.getId())) {
            System.out.println("channel id : " + chanRes.channel().getId() + " channel type : " + chanRes.channel().getType() + " 참석자 리스트(비공개채널일때만) : " + chanRes.attendeesId());
        }
        System.out.println("---------- 채널 update ----------");
        ChannelCreateResponse updatedChannelResponse = channelService.update(new ChannelUpdateRequest(댕댕공개채널_CreateResponse.channel().getId(), "댕댕이의 공개 채널 진화함", null));
        System.out.println(updatedChannelResponse.channel().getName() + " 로 업데이트 됨");

//        System.out.println("---------- 채널 update -> 결과 : private 채널이므로 에러 떠야함 ----------");
//        channelService.update(new ChannelUpdateRequest(냥냥비공개채널_CreateResponse.channel().getId(), "냥냥이의 비공개 채널 진화함", null));


        System.out.println("---------- 채널 delete 후 결과 조회  ----------");
        channelService.delete(댕댕공개채널2_CreateResponse.channel().getId());
        for (ChannelResponse chanRes : channelService.findAllByUserId(userDog.getId())) {
            System.out.println("channel id : " + chanRes.channel().getId() + "참석자 리스트(비공개채널일때만) : " + chanRes.attendeesId());
        }

        return 냥냥비공개채널_CreateResponse.channel();
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        message = new Message("안녕하세요. 저는 " + author.getName() + " 입니다.", author.getId(), channel.getId(), Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        messageService.create(message);
        System.out.println("메시지 생성: " + message.toString());
    }

    public static void main(String[] args) {

        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service Start🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️");
        deleteDirectoryContents(Paths.get(System.getProperty("user.dir"), "data").toFile());


        ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        /* 빈 목록 */
//        for (String beanName : context.getBeanDefinitionNames()) {
//            System.out.println("beanName :" + beanName);
//        }

        /*테스트를 위해 bean을 직접 불러옴 */
        UserService userService = context.getBean(BasicUserService.class);
        ChannelService channelService = context.getBean(BasicChannelService.class);
        MessageService messageService = context.getBean(BasicMessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);


//        messageCreateTest(messageService, channel, user);


        System.out.println("🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️Service End🏃‍♂️‍➡️🏃‍♂️‍➡️🏃‍♂️‍➡️️‍");
    }

    public static void deleteDirectoryContents(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("지정된 경로가 디렉토리가 아닙니다: " + directory.getAbsolutePath());
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) { // 디렉토리 안에 파일이 있다면
            for (File file : files) {
                if (file.isDirectory()) {
                    // 하위 디렉토리 내용도 재귀적으로 삭제
                    deleteDirectoryContents(file);
                }
                if (!file.delete()) {
                    System.out.println("삭제 실패: " + file.getAbsolutePath());
                }
            }
        }
    }
}

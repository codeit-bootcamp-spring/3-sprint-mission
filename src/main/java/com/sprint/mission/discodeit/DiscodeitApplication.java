package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    private static User 댕댕;
    private static User 냥냥;
    private static Channel 댕댕채널1_공개;
    private static Channel 댕댕채널2_공개;
    private static Channel 댕댕채널3_비공개;
    private static Channel 냥냥채널1_비공개;
    private static Message message;

    static User setupUser(UserService userService, BinaryContentService binaryContentService, UserStatusService userStatusService) {
        // 프로필사진 있는 유저 생성
        BinaryContentResponse 댕댕이프로필사진Response = binaryContentService.create(new BinaryContentCreateRequest(new File("./src/main/resources/sample_01.png")));
        UserCreateResponse 댕댕이유저ResponseWithProfile = userService.create(new UserCreateRequest("댕댕이", "woody@codeit.com", "woody1234", 댕댕이프로필사진Response.binaryContent().getId()));
        댕댕 = 댕댕이유저ResponseWithProfile.user();

        System.out.println("----------프로필있는 유저 생성----------");
        System.out.println(댕댕이유저ResponseWithProfile);

        // 프로필사진 없는 유저 생성
        UserCreateResponse 냥냥이유저Response = userService.create(new UserCreateRequest("냥냥이", "woody2@codeit.com", "woody1234", null));
        냥냥 = 냥냥이유저Response.user();

        System.out.println("----------프로필없는 유저 생성----------");
        System.out.println(냥냥이유저Response);


        // 테스트
        System.out.println("---------- 유저 findById ----------");
        System.out.println(userService.find(냥냥이유저Response.user().getId()).toString());
        System.out.println("---------- 유저 findAll ----------");
        for (UserResponse userRes : userService.findAll()) {
            System.out.println("name : " + userRes.name());
        }
        System.out.println("---------- 유저 update ----------");
        UserCreateResponse updatedUserResponse = userService.update(new UserUpdateRequest(댕댕이유저ResponseWithProfile.user().getId(), "댕댕 진화함", null, null, null));
        System.out.println(updatedUserResponse.user().getName() + " 로 업데이트 됨");

        System.out.println("---------- 유저상태 findAll ----------");
        for (UserStatusResponse userRes : userStatusService.findAll()) {
            System.out.println("name : " + userRes.userStatus());
        }
        System.out.println("---------- 유저상태 update ----------");
        System.out.println(userStatusService.updateByUserId(냥냥.getId(), UserStatusType.OFFLINE).userStatus().toString());

        System.out.println("---------- 유저 delete 후 결과 조회 ----------");
        userService.delete(댕댕이유저ResponseWithProfile.user().getId());

        System.out.println("---------- (삭제후) 유저 findAll ----------");
        for (UserResponse userRes : userService.findAll()) {
            System.out.println("name : " + userRes.name());
        }

        System.out.println("----------  (삭제후) 유저상태 findAll ----------");
        for (UserStatusResponse userRes : userStatusService.findAll()) {
            System.out.println("name : " + userRes.userStatus());
        }

        return 댕댕이유저ResponseWithProfile.user();
    }

    static Channel setupChannel(ChannelService channelService) {

        // 공개 채널 생성
        ChannelCreateResponse 댕댕공개채널_CreateResponse = channelService.create(new PublicChannelCreateRequest("댕댕이들의 공개 채널", ChannelType.PUBLIC, "댕댕 공지사항", 댕댕.getId()));
        ChannelCreateResponse 댕댕공개채널2_CreateResponse = channelService.create(new PublicChannelCreateRequest("댕댕이들의 공개 채널2", ChannelType.PUBLIC, "댕댕 공지사항2", 댕댕.getId()));
        댕댕채널1_공개 = 댕댕공개채널_CreateResponse.channel();
        댕댕채널2_공개 = 댕댕공개채널2_CreateResponse.channel();

        System.out.println("----------공개 채널 생성----------");
        System.out.println(댕댕공개채널_CreateResponse.toString());
        System.out.println(댕댕공개채널2_CreateResponse.toString());

        // 비공개 채널 생성
        ChannelCreateResponse 냥냥비공개채널_CreateResponse = channelService.create(new PrivateChannelCreateRequest(ChannelType.PRIVATE, 냥냥.getId()));
        ChannelCreateResponse 댕댕비공개채널_CreateResponse = channelService.create(new PrivateChannelCreateRequest(ChannelType.PRIVATE, 댕댕.getId()));
        냥냥채널1_비공개 = 냥냥비공개채널_CreateResponse.channel();
        댕댕채널3_비공개 = 댕댕비공개채널_CreateResponse.channel();


        System.out.println("----------비공개 채널 생성----------");
        System.out.println(냥냥비공개채널_CreateResponse.toString());
        System.out.println(댕댕비공개채널_CreateResponse.toString());

        // 테스트
        System.out.println("---------- 채널 findById ----------");
        System.out.println(channelService.find(댕댕공개채널_CreateResponse.channel().getId()).toString());
        System.out.println("---------- 채널 findAllByUserId (댕댕이들) ----------");
        for (ChannelResponse chanRes : channelService.findAllByUserId(댕댕.getId())) {
            System.out.println("channel id : " + chanRes.channel().getId() + " channel type : " + chanRes.channel().getType() + " 참석자 리스트(비공개채널일때만) : " + chanRes.attendeesId());
        }
        System.out.println("---------- 채널 update ----------");
        ChannelCreateResponse updatedChannelResponse = channelService.update(new ChannelUpdateRequest(댕댕공개채널_CreateResponse.channel().getId(), "댕댕이의 공개 채널 진화함", null));
        System.out.println(updatedChannelResponse.channel().getName() + " 로 업데이트 됨");

//        System.out.println("---------- 채널 update -> 결과 : private 채널이므로 에러 떠야함 ----------");
//        channelService.update(new ChannelUpdateRequest(냥냥비공개채널_CreateResponse.channel().getId(), "냥냥이의 비공개 채널 진화함", null));


        System.out.println("---------- 채널 delete 후 결과 조회  ----------");
        channelService.delete(댕댕공개채널2_CreateResponse.channel().getId());
        for (ChannelResponse chanRes : channelService.findAllByUserId(댕댕.getId())) {
            System.out.println("channel id : " + chanRes.channel().getId() + "참석자 리스트(비공개채널일때만) : " + chanRes.attendeesId());
        }

        return 냥냥비공개채널_CreateResponse.channel();
    }

    static void messageCreateTest(MessageService messageService) {

        // '댕댕채널1_공개' 메세지 생성
        MessageCreateResponse 댕댕이메세지ResponseWithAttachments = messageService.create(new MessageCreateRequest("댕댕이의 메세지예요", 댕댕.getId(), 댕댕채널1_공개.getId(), List.of(UUID.randomUUID(), UUID.randomUUID())));
        MessageCreateResponse 댕댕이메세지2Response = messageService.create(new MessageCreateRequest("댕댕이의 두번째 메세지예요", 댕댕.getId(), 댕댕채널1_공개.getId(), null));


        System.out.println("----------'댕댕채널1_공개' 메세지 생성----------");
        System.out.println(댕댕이메세지ResponseWithAttachments.message().toString());
        System.out.println(댕댕이메세지2Response.message().toString());

        // '냥냥채널1_비공개' 메세지 생성
        MessageCreateResponse 냥냥이메세지ResponseWithAttachments = messageService.create(new MessageCreateRequest("냥냥이의 메세지예요", 냥냥.getId(), 냥냥채널1_비공개.getId(), List.of(UUID.randomUUID(), UUID.randomUUID())));
        MessageCreateResponse 냥냥이메세지2Response = messageService.create(new MessageCreateRequest("냥냥이의 두번째 메세지예요", 냥냥.getId(), 냥냥채널1_비공개.getId(), null));
        System.out.println("----------'냥냥채널1_비공개' 메세지 생성----------");
        System.out.println(냥냥이메세지ResponseWithAttachments.message().toString());
        System.out.println(냥냥이메세지2Response.message().toString());


        // 테스트
        System.out.println("---------- 메세지 findById ----------");
        System.out.println(messageService.findById(댕댕이메세지ResponseWithAttachments.message().getId()).toString());
        System.out.println("---------- 메세지 findAllByChannelId ----------");
        for (MessageCreateResponse messageResponse : messageService.findAllByChannelId(냥냥채널1_비공개.getId())) {
            System.out.println(messageResponse.message().toString());
        }
        System.out.println("---------- 메세지 update ----------");
        MessageCreateResponse updatedMessageResponse = messageService.update(new MessageUpdateRequest(냥냥이메세지2Response.message().getId(), "냥냥이의 업데이트된 두번째 메세지예요", null));
        System.out.println(updatedMessageResponse.message().toString() + " 로 업데이트 됨");

        System.out.println("---------- 메세지 delete 후 결과 조회 ----------");
        messageService.delete(냥냥이메세지2Response.message().getId());
        System.out.println("---------- 메세지 findAllByChannelId ----------");
        for (MessageCreateResponse messageResponse : messageService.findAllByChannelId(냥냥채널1_비공개.getId())) {
            System.out.println(messageResponse.message().toString());
        }
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
        BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);
        UserStatusService userStatusService = context.getBean(UserStatusService.class);

        // 셋업
        User user = setupUser(userService, binaryContentService, userStatusService);
        Channel channel = setupChannel(channelService);
//        messageCreateTest(messageService);


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

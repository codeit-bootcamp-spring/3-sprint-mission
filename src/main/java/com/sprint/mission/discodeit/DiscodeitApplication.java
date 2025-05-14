package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
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
        Optional<BinaryContentCreateRequest> 댕댕이프로필사진 = Optional.of(new BinaryContentCreateRequest(new File("./src/main/resources/sample_01.png")));
        User 댕댕이유저WithProfile = userService.create(new UserCreateRequest("댕댕이", "woody@codeit.com", "woody1234"), 댕댕이프로필사진);
        댕댕 = 댕댕이유저WithProfile;

        System.out.println("----------프로필있는 유저 생성----------");
        System.out.println(댕댕이유저WithProfile);

        // 프로필사진 없는 유저 생성
        User 냥냥이유저 = userService.create(new UserCreateRequest("냥냥이", "woody2@codeit.com", "woody1234"), Optional.empty());
        냥냥 = 냥냥이유저;

        System.out.println("----------프로필없는 유저 생성----------");
        System.out.println(냥냥이유저);


        // 테스트
        System.out.println("---------- 유저 findById ----------");
        System.out.println(userService.find(냥냥이유저.getId()).toString());
        System.out.println("---------- 유저 findAll ----------");
        for (UserResponse userRes : userService.findAll()) {
            System.out.println(userStatusService.updateByUserId(냥냥.getId(), UserStatusType.OFFLINE).toString());

//        System.out.println("---------- 유저 delete 후 결과 조회 ----------");
//        userService.delete(댕댕이유저ResponseWithProfile.user().getId());
//
            System.out.println("name : " + userRes.name());
        }
        System.out.println("---------- 유저 update ----------");
        User updatedUser = userService.update(댕댕이유저WithProfile.getId(), new UserUpdateRequest("댕댕 진화함", null, null), Optional.empty());
        System.out.println(updatedUser.getName() + " 로 업데이트 됨");

        System.out.println("---------- 유저상태 findAll ----------");
        for (UserStatus userStatus : userStatusService.findAll()) {
            System.out.println("name : " + userStatus);
        }
        System.out.println("---------- 유저상태 update ----------");
//        System.out.println("---------- (삭제후) 유저 findAll ----------");
//        for (UserResponse userRes : userService.findAll()) {
//            System.out.println("name : " + userRes.name());
//        }
//
//        System.out.println("----------  (삭제후) 유저상태 findAll ----------");
//        for (UserStatusResponse userRes : userStatusService.findAll()) {
//            System.out.println("name : " + userRes.userStatus());
//        }

        return 댕댕이유저WithProfile;
    }

    static Channel setupChannel(ChannelService channelService, ReadStatusService readStatusService) {

        // 공개 채널 생성
        Channel 댕댕공개채널 = channelService.create(new PublicChannelCreateRequest("댕댕이들의 공개 채널", ChannelType.PUBLIC, "댕댕 공지사항", 댕댕.getId()));
        Channel 댕댕공개채널2 = channelService.create(new PublicChannelCreateRequest("댕댕이들의 공개 채널2", ChannelType.PUBLIC, "댕댕 공지사항2", 댕댕.getId()));
        댕댕채널1_공개 = 댕댕공개채널;
        댕댕채널2_공개 = 댕댕공개채널2;

        System.out.println("----------공개 채널 생성----------");
        System.out.println(댕댕공개채널.toString());
        System.out.println(댕댕공개채널2.toString());

        // 비공개 채널 생성
        Channel 냥냥비공개채널 = channelService.create(new PrivateChannelCreateRequest(ChannelType.PRIVATE, 냥냥.getId()));
        Channel 댕댕비공개채널 = channelService.create(new PrivateChannelCreateRequest(ChannelType.PRIVATE, 댕댕.getId()));
        냥냥채널1_비공개 = 냥냥비공개채널;
        댕댕채널3_비공개 = 댕댕비공개채널;


        System.out.println("----------비공개 채널 생성----------");
        System.out.println(냥냥비공개채널.toString());
        System.out.println(댕댕비공개채널.toString());

        // 테스트
        System.out.println("---------- 채널 findById ----------");
        System.out.println(channelService.find(댕댕공개채널.getId()).toString());
        System.out.println("---------- 채널 findAllByUserId (댕댕이들) ----------");
        for (ChannelResponse chanRes : channelService.findAllByUserId(댕댕.getId())) {
            System.out.println("channel id : " + chanRes.channel().getId() + " channel type : " + chanRes.channel().getType() + " 참석자 리스트(비공개채널일때만) : " + chanRes.attendeesId());
        }
        System.out.println("---------- 채널 update ----------");
        Channel updatedChannel = channelService.update(댕댕공개채널.getId(), new PublicChannelUpdateRequest("댕댕이의 공개 채널 진화함", null));
        System.out.println(updatedChannel.getName() + " 로 업데이트 됨");

//        System.out.println("---------- 채널 update -> 결과 : private 채널이므로 에러 떠야함 ----------");
//        channelService.update(new ChannelUpdateRequest(냥냥비공개채널_CreateResponse.channel().getId(), "냥냥이의 비공개 채널 진화함", null));

        System.out.println("---------- ReadStatus findAll ----------");
        for (ReadStatus readStatus : readStatusService.findAllByUserId(댕댕.getId())) {
            System.out.println("name : " + readStatus.toString());
        }

        channelService.delete(댕댕공개채널2.getId());

        return 냥냥비공개채널;
    }

    static void messageCreateTest(MessageService messageService) {

        // '댕댕채널1_공개' 메세지 생성
        Message 댕댕이메세지WithAttachments = messageService.create(new MessageCreateRequest("댕댕이의 메세지예요", 댕댕.getId(), 댕댕채널1_공개.getId(), List.of(UUID.randomUUID(), UUID.randomUUID())));
        Message 댕댕이메세지2 = messageService.create(new MessageCreateRequest("댕댕이의 두번째 메세지예요", 댕댕.getId(), 댕댕채널1_공개.getId(), null));


        System.out.println("----------'댕댕채널1_공개' 메세지 생성----------");
        System.out.println(댕댕이메세지WithAttachments.toString());
        System.out.println(댕댕이메세지2.toString());

        // '냥냥채널1_비공개' 메세지 생성
        Message 냥냥이메세지WithAttachments = messageService.create(new MessageCreateRequest("냥냥이의 메세지예요", 냥냥.getId(), 냥냥채널1_비공개.getId(), List.of(UUID.randomUUID(), UUID.randomUUID())));
        Message 냥냥이메세지2 = messageService.create(new MessageCreateRequest("냥냥이의 두번째 메세지예요", 냥냥.getId(), 냥냥채널1_비공개.getId(), null));
        System.out.println("----------'냥냥채널1_비공개' 메세지 생성----------");
        System.out.println(냥냥이메세지WithAttachments.toString());
        System.out.println(냥냥이메세지2.toString());


        // 테스트
        System.out.println("---------- 메세지 findById ----------");
        System.out.println(messageService.findById(댕댕이메세지WithAttachments.getId()).toString());
        System.out.println("---------- 메세지 findAllByChannelId ----------");
        for (Message message : messageService.findAllByChannelId(냥냥채널1_비공개.getId())) {
            System.out.println(message.toString());
        }
        System.out.println("---------- 메세지 update ----------");
        Message updatedMessage = messageService.update(냥냥이메세지2.getId(), new MessageUpdateRequest("냥냥이의 업데이트된 두번째 메세지예요", null));
        System.out.println(updatedMessage.toString() + " 로 업데이트 됨");

        System.out.println("---------- 메세지 delete 후 결과 조회 ----------");
        messageService.delete(냥냥이메세지2.getId());
        System.out.println("---------- 메세지 findAllByChannelId ----------");
        for (Message message : messageService.findAllByChannelId(냥냥채널1_비공개.getId())) {
            System.out.println(message.toString());
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
        BinaryContentService binaryContentService = context.getBean(BasicBinaryContentService.class);
        UserStatusService userStatusService = context.getBean(BasicUserStatusService.class);
        ReadStatusService readStatusService = context.getBean(BasicReadStatusService.class);

        // 셋업
        User user = setupUser(userService, binaryContentService, userStatusService);
        Channel channel = setupChannel(channelService, readStatusService);
        messageCreateTest(messageService);


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

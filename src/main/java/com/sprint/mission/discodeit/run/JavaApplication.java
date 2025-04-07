package com.sprint.mission.discodeit.run;

import java.util.Set;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {

    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService(userService);
        JCFMessageService messageService = new JCFMessageService(userService, channelService);

        //1. User Domain
        User user1 = userService.createUser("Hansel", "Hansel@gmail.com", "0000");
        User user2 = userService.createUser("Gretel", "Gretel@gmail.com", "1111");
        User user3 = userService.createUser("Witch", "Witch@gmail.com", "2222");
        User user4 = userService.createUser("Mike", "Mike@gmail.com", "3333");
        User user5 = userService.createUser("Jack", "Jack@gmail.com", "4444");

        System.out.println("=== (User)사용자 등록===");
        System.out.println("사용자 1: " + user1.getUserName() + ", ID: " + user1.getEmail() + " password: " + user1.getPassword());
        System.out.println("사용자 2: " + user2.getUserName() + ", ID: " + user2.getEmail() + " password: " + user2.getPassword());
        System.out.println("사용자 3: " + user3.getUserName() + ", ID: " + user3.getEmail() + " password: " + user3.getPassword());
        System.out.println("사용자 4: " + user4.getUserName() + ", ID: " + user4.getEmail() + " password: " + user4.getPassword());
        System.out.println("사용자 5: " + user5.getUserName() + ", ID: " + user5.getEmail() + " password: " + user5.getPassword());

        System.out.println("\n=== (User)단일 사용자 조회 ===");
        System.out.println(userService.getUserById(user1.getUserId()));

        System.out.println("\n=== (User)모든 사용자 조회 ===");
        userService.getAllUsers().forEach(System.out::println);

        // 사용자명 수정 및 비교
        System.out.println("\n=== (User)사용자명 수정 ===");
        System.out.println("수정 전: " + userService.getUserById(user1.getUserId()).getUserName());
        userService.updateUserName(user1.getUserId(), "헨젤");
        System.out.println("수정 후: " + userService.getUserById(user1.getUserId()).getUserName());

        // 이메일 수정 및 비교
        System.out.println("\n=== (User)사용자 이메일 수정 ===");
        System.out.println("수정 전: " + userService.getUserById(user1.getUserId()).getEmail());
        userService.updateUserEmail(user1.getUserId(), "updated@gmail.com");
        System.out.println("수정 후: " + userService.getUserById(user1.getUserId()).getEmail());

        // 비밀번호 수정 및 비교
        System.out.println("\n=== (User)사용자 비밀번호 수정 ===");
        System.out.println("수정 전: " + userService.getUserById(user1.getUserId()).getPassword());
        userService.updateUserPassword(user1.getUserId(), "1234");
        System.out.println("수정 후: " + userService.getUserById(user1.getUserId()).getPassword());

        System.out.println("\n=== (User)수정된 데이터 조회 ===");
        System.out.println(userService.getUserById(user1.getUserId()));

        System.out.println("\n=== (User)사용자 삭제 ===");
        userService.deleteUser(user1.getUserId());

        System.out.println("\n=== (User)삭제 후 전체 사용자 조회 ===");
        userService.getAllUsers().forEach(System.out::println);

        //2. Channel Domain
        System.out.println("=== (Channel)채널 등록 완료===");
        Channel channel1 = channelService.createChannel("1번 방", false, "", user5.getUserId()); //1번방 방장 Jack
        Channel channel2 = channelService.createChannel("2번 방", true, "1234", user2.getUserId()); //2번방 방장 Gretel

        System.out.println("\n=== (Channel)채널 참가 성공===");
        channelService.joinChannel(channel1.getChannelId(), user2.getUserId(), ""); //1번방 참가자 Gretel
        channelService.joinChannel(channel2.getChannelId(), user3.getUserId(), "1234"); //2번방 참가자 witch
//        System.out.println("\n=== (Channel)채널 참가 실패===");
//        channelService.joinChannel(channel1.getChannelId(), user1.getUserId(), ""); //채팅 개설자는 이미 참가하였음
//        channelService.joinChannel(channel2.getChannelId(), user4.getUserId(), "4321"); // 비밀번호 불일치

        System.out.println("\n=== (Channel)채널 참가자 확인 ===");
        channelService.getAllChannels().forEach(channel -> {
            System.out.println("채널명: " + channel.getChannelName());
            Set<UUID> participants = channelService.getChannelParticipants(channel.getChannelId());
            System.out.println("참가자 수: " + participants.size());
            System.out.println("참가자 목록:");
            participants.forEach(userId -> {
                User participant = userService.getUserById(userId);
                if (participant != null) {
                    System.out.println("- " + participant.getUserName()
                            + (userId.equals(channel.getOwnerChannelId()) ? " (방장)" : ""));
                }
            });
            System.out.println(); // 채널 구분을 위한 newline
        });

        System.out.println("\n=== (Channel)채널 떠나기 ===");
        channelService.leaveChannel(channel1.getChannelId(), user2.getUserId());

        System.out.println("\n=== (Channel)단일 채널 조회 ===");
        System.out.println(channelService.getChannelById(channel1.getChannelId()));

        System.out.println("\n=== (Channel)모든 채널 조회 ===");
        channelService.getAllChannels().forEach(System.out::println);

        System.out.println("\n=== (Channel)채널 정보 수정 ===");
        System.out.println("\n=== (Channel)채널 수정 전 조회 ===");
        System.out.println(channelService.getChannelById(channel1.getChannelId()));

        System.out.println("\n=== (Channel)채널 수정 후  조회 ===");
        channelService.updateChannel(channel1.getChannelId(),"첫번째 방",false, "");
        System.out.println(channelService.getChannelById(channel1.getChannelId()));

        System.out.println("\n=== (Channel)채널 삭제 ===");
        channelService.deleteChannel(channel2.getChannelId());

        System.out.println("\n=== (Channel)삭제후 전체 채널 조회 ===");
        channelService.getAllChannels().forEach(System.out::println);

        System.out.println("\n=== (Channel) 도메인 검증 테스트 ===");

        // 1. 존재하지 않는 사용자로 채널 생성 시도
        System.out.println("\n--- 존재하지 않는 소유자로 채널 생성 시도 ---");
        try {
            channelService.createChannel("유령 채널", false, "", UUID.randomUUID());
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생 (정상): " + e.getMessage());
        }

        // 2. 존재하지 않는 채널에 참가 시도
        System.out.println("\n--- 존재하지 않는 채널에 참가 시도 ---");
        try {
            channelService.joinChannel(UUID.randomUUID(), user3.getUserId(), "");
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생 (정상): " + e.getMessage());
        }

        // 3. 존재하지 않는 사용자로 채널 참가 시도
        System.out.println("\n--- 존재하지 않는 사용자로 채널 참가 시도 ---");
        try {
            channelService.joinChannel(channel1.getChannelId(), UUID.randomUUID(), "");
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생 (정상): " + e.getMessage());
        }

        // 4. 비공개 채널에 잘못된 비밀번호로 참가 시도
        System.out.println("\n--- 비공개 채널 잘못된 비밀번호로 참가 시도 ---");
        // 테스트를 위해 새 비공개 채널 생성 (기존 channel2는 삭제됨)
        Channel privateChannel = channelService.createChannel("비밀방", true, "correct_password", user4.getUserId());
        try {
            channelService.joinChannel(privateChannel.getChannelId(), user3.getUserId(), "wrong_password");
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생 (정상): " + e.getMessage());
        }
        // 정상 참여 시도 (참가자 확인용)
        System.out.println("비밀방 정상 참여 시도 결과: " + channelService.joinChannel(privateChannel.getChannelId(), user3.getUserId(), "correct_password"));


        // 5. 채널 소유자가 채널 떠나기 시도
        System.out.println("\n--- 채널 소유자가 채널 떠나기 시도 ---");
        try {
            channelService.leaveChannel(privateChannel.getChannelId(), user4.getUserId()); // user4가 소유자
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생 (정상): " + e.getMessage());
        }

        // 6. 존재하지 않는 채널의 참여자 조회 시도
        System.out.println("\n--- 존재하지 않는 채널 참여자 조회 시도 ---");
        try {
            channelService.getChannelParticipants(UUID.randomUUID());
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생 (정상): " + e.getMessage());
        }

        // 테스트용으로 생성한 비공개 채널 정리
        System.out.println("\n--- 테스트용 비공개 채널 삭제 ---");
        channelService.deleteChannel(privateChannel.getChannelId());
        System.out.println("삭제 후 채널 수: " + channelService.getAllChannels().size());

        //3. Message Domain
        System.out.println("\n\n=== (Message)메세지 생성===");
        
        Message message1 = messageService.createMessage("Hello everyone in Channel 1", user5.getUserId(), channel1.getChannelId());
        Message message2 = messageService.createMessage("It's me again!", user5.getUserId(), channel1.getChannelId());
        System.out.println("메시지 1 생성: " + message1.getContent());
        System.out.println("메시지 2 생성: " + message2.getContent());

        System.out.println("\n=== (Message)ID를 통해 메시지 조회 ===");
        System.out.println(messageService.getMessageById(message1.getMessageId()));

        System.out.println("\n=== (Message)특정 채널의 모든 메시지 조회 ===");
        messageService.getMessagesByChannel(channel1.getChannelId()).forEach(System.out::println);

        System.out.println("\n=== (Message)특정 작성자의 모든 메시지 조회 (user5) ===");
        messageService.getMessagesByAuthor(user5.getUserId()).forEach(System.out::println);

        System.out.println("\n=== (Message)메세지 정보 수정 전 ===");
        System.out.println(messageService.getMessageById(message1.getMessageId()));

        System.out.println("\n=== (Message)메세지 정보 수정 후 ===");
        messageService.updateMessage(message1.getMessageId(), "채널1 여러분 안녕하세요");
        System.out.println(messageService.getMessageById(message1.getMessageId()));

        System.out.println("\n=== (Message)수정된 메시지 조회 ===");
        messageService.getMessagesByChannel(channel1.getChannelId()).forEach(System.out::println);

        System.out.println("\n=== (Message)메시지 삭제 ===");
        messageService.deleteMessage(message2.getMessageId());

        System.out.println("\n=== (Message)삭제후 해당 채널의 전체 메시지 조회 ===");
        messageService.getMessagesByChannel(channel1.getChannelId()).forEach(System.out::println);

        System.out.println("\n=== (Message) 도메인 검증 테스트 ===");

        // 1. 존재하지 않는 작성자로 메시지 생성 시도
        System.out.println("\n--- 존재하지 않는 작성자로 메시지 생성 시도 ---");
        try {
            messageService.createMessage("유령 메시지", UUID.randomUUID(), channel1.getChannelId());
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생 (정상): " + e.getMessage());
        }

        // 2. 존재하지 않는 채널에 메시지 생성 시도
        System.out.println("\n--- 존재하지 않는 채널에 메시지 생성 시도 ---");
        try {
            messageService.createMessage("어디로 가는 메시지?", user5.getUserId(), UUID.randomUUID());
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생 (정상): " + e.getMessage());
        }

        // 3. 채널에 참여하지 않은 사용자가 메시지 생성 시도
        System.out.println("\n--- 채널 미참여자가 메시지 생성 시도 ---");
        try {
            messageService.createMessage("나 여기 없는데?", user4.getUserId(), channel1.getChannelId());
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생 (정상): " + e.getMessage());
        }
    }
}

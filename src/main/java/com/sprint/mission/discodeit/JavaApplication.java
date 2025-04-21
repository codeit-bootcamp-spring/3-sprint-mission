package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

public class JavaApplication {
    public static void main(String[] args) {
        // 서비스 생성
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        // 1. 사용자 생성
        User user1 = userService.create("김민준");
        User user2 = userService.create("김이월");

        // 2. 채널 생성
        Channel channel = channelService.create("일반채널");

        // 3. 메시지 생성
        Message msg1 = messageService.create(user1.getId(), channel.getId(), "안녕하세요!");
        Message msg2 = messageService.create(user2.getId(), channel.getId(), "반가워요!");

        // 4. 출력
        System.out.println("유저 목록: " + userService.findAll());
        System.out.println("채널 목록: " + channelService.findAll());
        System.out.println("메시지 목록: " + messageService.findAll());

        // 5. 메시지 수정
        messageService.update(msg1.getId(), "수정된 인사!");
        System.out.println("수정된 메시지: " + messageService.findById(msg1.getId()));

        // 6. 메시지 삭제
        messageService.delete(msg2.getId());
        System.out.println("삭제 후 메시지 목록: " + messageService.findAll());
    }
}

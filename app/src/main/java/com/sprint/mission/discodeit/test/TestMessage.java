package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;

public class TestMessage {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        System.out.println("✅ [MESSAGE 테스트]");

        // 사용자와 채널 생성
        User user = userService.create("민준");
        Channel channel = channelService.create("채팅방");

        // 메시지 생성
        Message message = messageService.create(user.getId(), channel.getId(), "안녕하세요!");
        System.out.println("생성된 메시지: " + message);

        // 단건 조회
        Message found = messageService.findById(message.getId());
        System.out.println("조회된 메시지: " + found);

        // 수정
        messageService.update(message.getId(), "수정된 메시지입니다.");
        System.out.println("수정된 메시지: " + messageService.findById(message.getId()));

        // 전체 조회
        System.out.println("전체 메시지 목록: " + messageService.findAll());

        // 삭제
        messageService.delete(message.getId());
        System.out.println("삭제 후 조회: " + messageService.findById(message.getId()));
    }
}

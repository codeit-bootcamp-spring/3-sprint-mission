package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;

public class TestChannel {
    public static void main(String[] args) {
        JCFChannelService channelService = new JCFChannelService();

        System.out.println("✅ [CHANNEL 테스트]");

        // 생성
        Channel channel = channelService.create("공지사항");
        System.out.println("생성된 채널: " + channel);

        // 단건 조회
        Channel found = channelService.findById(channel.getId());
        System.out.println("조회된 채널: " + found);

        // 수정
        channelService.update(channel.getId(), "일반채널");
        System.out.println("수정된 채널 이름: " + channelService.findById(channel.getId()));

        // 전체 조회
        System.out.println("전체 채널 목록: " + channelService.findAll());

        // 삭제
        channelService.delete(channel.getId());
        System.out.println("삭제 후 조회: " + channelService.findById(channel.getId()));
    }
}

package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;


public class JavaApplication2 {
    public static void main(String[] args) {
        // FileIO 기반 구현체로 교체
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService();

        // 테스트 데이터
        User user = userService.create("김민준");
        Channel channel = channelService.create("일반채널");
        Message message = messageService.create("안녕하세요!", channel.getId(), user.getId());

        // 결과 출력
        System.out.println("👤 유저: " + user);
        System.out.println("📺 채널: " + channel);
        System.out.println("💬 메시지: " + message);
    }
}

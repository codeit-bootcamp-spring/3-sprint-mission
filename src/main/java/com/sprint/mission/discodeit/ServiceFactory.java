package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.jcf.*;
import com.sprint.mission.discodeit.service.*;

public class ServiceFactory {

    // 사용자 서비스 생성
    public static UserService createUserService() {
        return new JCFUserService();
    }

    // 채널 서비스 생성
    public static ChannelService createChannelService() {
        return new JCFChannelService();
    }

    // 메시지 서비스 생성 (의존성 주입)
    public static MessageService createMessageService(UserService userService, ChannelService channelService) {
        return new JCFMessageService(userService, channelService);
    }
}
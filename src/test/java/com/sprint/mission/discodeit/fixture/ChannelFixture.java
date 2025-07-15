package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class ChannelFixture {

    public static Channel createPublicChannel(String channelName, String description) {
        Channel publicChannel = new Channel(ChannelType.PUBLIC, channelName, description);
        ReflectionTestUtils.setField(publicChannel, "id", UUID.randomUUID());
        return publicChannel;
    }

    public static Channel createPrivateChannel() {
        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        ReflectionTestUtils.setField(privateChannel, "id", UUID.randomUUID());
        return privateChannel;
    }

    public static Channel createChannel(ChannelType channelType, String name, String description) {
        Channel channel = new Channel(channelType, name, description);
        ReflectionTestUtils.setField(channel, "id", UUID.randomUUID());
        return channel;
    }
}

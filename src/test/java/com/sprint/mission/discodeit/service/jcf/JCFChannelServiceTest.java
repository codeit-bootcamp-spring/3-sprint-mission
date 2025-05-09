package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JCFChannelServiceTest {

    private ChannelService channelService;
    private UserService userService;
    private User creator;
    private Channel channel;

    @BeforeEach
    void setUp() {
        channelService = new JCFChannelService();
        userService = new JCFUserService();
        creator = userService.create("심슨", "sjo06102", "!qwe0123", "sjo06102@gmail.com");
        channel = channelService.create("스프링필드", "심슨네 가족", false, creator, "1234");

    }

    @Test
    void createChannel() {

        String ChannelName = "롤 내전";
        String ChannelDescription = "수학과 내전 방입니다.";
        boolean isLock = false;
        User creator = userService.create("바트", "sjo06103", "!qwe1234", "sjo06102@naver.com");
        String password = "1234";

        Channel testchannel = channelService.create(ChannelName, ChannelDescription, isLock, creator, password);

        assertAll(
                () -> assertNotNull(testchannel),
                () -> assertEquals(ChannelName, testchannel.getChannelName(), "생성된 채널의 이름이 다릅니다."),
                () -> assertEquals(ChannelDescription, testchannel.getChannelDescription(), "생성된 채널의 소개가 다릅니다."),
                () -> assertEquals(isLock, testchannel.isLock(), "생성된 채널의 공개 여부가 다릅니다."),
                () -> assertEquals(creator, testchannel.getCreator(), "생성된 채널의 어드민이 다릅니다."),
                () -> assertEquals(password, testchannel.getPassword(), "생성된 채널의 비밀번호가 다릅니다.")
        );

    }

    @Test
    void findChannelByUUID() {

        assertAll(
                () -> assertNotNull(channelService.readById(channel.getId()), "찾는 채널이 존재하지 않습니다."),
                () -> assertEquals(channel, channelService.readById(channel.getId()), "찾는 채널과 일치하지가 않습니다.")
        );

    }

    @Test
    void findAllChannels() {

        assertNotNull(channelService.readAll(), "채널 리스트가 Null입니다.");

    }

    @Test
    void updateChannel() {

        channelService.update(channel, "피파 토너먼트",
                "이제부터는 피파방입니다", true, "!qwe0123");


        assertAll(
                () -> assertNotNull(channel, "채널이 Null입니다."),
                () -> assertEquals("!qwe0123", channelService.readById(channel.getId()).getPassword(),
                        "수정된 패스워드와 일치하지 않습니다.")
        );
    }

    @Test
    void deleteChannel() {

        channelService.deleteById(creator, channel);
        assertNull(channelService.readById(channel.getId()), "채널이 삭제되지 않았습니다.");
    }

}

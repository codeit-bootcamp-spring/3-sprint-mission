package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChannelRepository {
    public static List<Channel> getChannel() {
        return new ArrayList<>(Arrays.asList(
                new Channel("코드잇 스프린트:스프링 백엔드 3기"),
                new Channel("코드잇 커뮤니티"),
                new Channel("알고리즘 스터디"),
                new Channel("CS 스터디")
        ));
    }
}

package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;

public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> channels = new ArrayList<>();
    // 초기 채널 입력
    public JCFChannelRepository(){
        channels.add(new Channel("ch01","default Channel","관리자",1744036548250L,1744036548250L));
    }
    // 채널 목록 반환
    public List<Channel> getChannelsList() {
        return channels;
    }
    public void fileSaveChannels(){
    }
}

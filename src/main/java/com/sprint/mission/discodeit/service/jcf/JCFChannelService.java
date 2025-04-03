package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf
 * fileName       : JCFChannelService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public UUID createChannel(List<User> channelUsers) {
        Channel channel = new Channel(channelUsers);
        data.add(channel);
        return channel.getId();
    }

    @Override
    public List<Channel> findChannelsById(UUID id) {

        return data.stream().filter(channel -> channel.getId().equals(id)).collect(Collectors.toList());

    }

    @Override
    public List<Channel> findAllChannel() {
        return data;
    }

    @Override
    public void updateChannelName(UUID id, String title) {

        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                channel.setTitle(title);
                System.out.println("업데이트 성공");
            }
        }
    }

    @Override
    public void deleteChannel(UUID id) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(id)) {
                data.remove(i);
            }
        }
    }
}

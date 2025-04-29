package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;
import java.util.stream.Collectors;

// JCF 활용, 데이터를 저장할 수 있는 필드(data)를 final 로 선언, 생성자에서 초기화
// data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현
public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService(Map<UUID, Channel> data) {
        this.data = new HashMap<UUID, Channel>(data);
    }



    // Create Override
    @Override
    public void createChannel(Channel channel) {        // 새로운 채널 생성
        data.put(channel.getChannelId(), channel);
    }


    // Read Override
    @Override
    public Channel readChannel(UUID id) {       // 특정 ID값의 채널 조회
        return data.get(id);
    }

    @Override
    public List<Channel> readChannelByName(String name) {
        return data.values().stream()
                .filter(channel -> channel.getChannelName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> readChannelByType(String type) {       // 특정 종류( Text, Voice )의 채널명 다중 조회
        return data.values().stream()           // Stream API 적용
                .filter(channel -> channel.getChannelType().equals(type))       // 채널 중 찾고자하는 값과 동일한 타입의 채널명만 필터링
                .collect(Collectors.toList());      // 해당 정보를 List 자료형으로 저장
    }

    @Override
    public List<Channel> readAllChannels() {        // 전체 채널 조회
        return new ArrayList<>(data.values());
    }


    // Update Override
    @Override
    public Channel updateChannel(UUID id, Channel channel) {    // 특정 ID의 채널 정보 수정
        if (data.containsKey(id)) {     // 파라미터로 받은 값이 존재하는지 탐색
            data.put(id, channel);      // 덮어씌우기
            return channel;
        }
        return null;
    }


    // Delete Override
    @Override
    public boolean deleteChannel(UUID id) {     // 특정 ID의 채널 제거
        return data.remove(id) != null;
    }
}

package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFReadStatusService implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> readStatusMap = new LinkedHashMap<>();


    // 저장
    @Override
    public ReadStatus saveReadStatus(ReadStatus readStatus) {
        readStatusMap.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    // 아이디로 조회
    @Override
    public Optional<ReadStatus> findReadStatusById(UUID id) {
        return Optional.ofNullable(readStatusMap.get(id));
    }

    // 유저 아이디로 조회
    @Override
    public List<ReadStatus> findUserReadStatus(UUID userId) {
        return readStatusMap.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    // 채널 아이디로 조회
    @Override
    public List<ReadStatus> findReadStatusByChannelId(UUID channelId) {
        return readStatusMap.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    // 수정
    @Override
    public void updateReadStatus(ReadStatus readStatus) {
        this.saveReadStatus(readStatus);
    }

    // 삭제
    @Override
    public void deleteReadStatusById(UUID id) {
        readStatusMap.remove(id);
    }

    // 채널 아이디로 삭제
    @Override
    public void deleteByChannelId(UUID channelId) {
        this.findReadStatusByChannelId(channelId)
                .forEach(readStatus -> deleteReadStatusById(readStatus.getId()));
    }
}

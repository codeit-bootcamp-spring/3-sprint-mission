package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFReadStatusRepository implements ReadStatusRepository {

    Map<UUID, ReadStatus> readStatusMap;
    Map<UUID, List<ReadStatus>> readStatusMapWithChannelId;
    Map<UUID, List<ReadStatus>> readStatusMapWithUserId;

    public JCFReadStatusRepository() {
        this.readStatusMap = new HashMap<>(); // 이게 얼마나 비효율적일까
        this.readStatusMapWithChannelId = new HashMap<>();
        this.readStatusMapWithUserId = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        // 1. id를 key로 저장
        readStatusMap.put(readStatus.getId(), readStatus);

        // 2. channelId에 해당하는 ReadStatus 리스트를 가져와서 없으면 새로 생성 후 추가
        List<ReadStatus> readStatusListWithChannelId = readStatusMapWithChannelId
                .getOrDefault(readStatus.getChannelId(), new ArrayList<>());
        readStatusListWithChannelId.add(readStatus);
        readStatusMapWithChannelId.put(readStatus.getChannelId(), readStatusListWithChannelId);

        // 3. userId에 해당하는 ReadStatus 리스트를 가져와서 없으면 새로 생성 후 추가
        List<ReadStatus> readStatusListWithUserId = readStatusMapWithUserId
                .getOrDefault(readStatus.getUserId(), new ArrayList<>());
        readStatusListWithUserId.add(readStatus);
        readStatusMapWithUserId.put(readStatus.getUserId(), readStatusListWithUserId);

        ReadStatus result = readStatusMap.get(readStatus.getId());

        return result;
    }

    @Override
    public ReadStatus findById(UUID id) {
        ReadStatus result = readStatusMap.get(id);
        return result;
    }

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId) {
        List<ReadStatus> result = readStatusMapWithChannelId.get(channelId);
        return result;
    }

    @Override
    public List<ReadStatus> findAll() {
        List<ReadStatus> result = new ArrayList<>();
        result.addAll(readStatusMap.values());
        return result;
    }

    @Override
    public boolean existByChannelIdAndUserId(UUID userId, UUID channelId) {
        // 1. channelId에 해당하는 ReadStatus 리스트 가져오기
        List<ReadStatus> readStatusListWithChannelId = readStatusMapWithChannelId.getOrDefault(channelId, new ArrayList<>());

        // 2. userId와 일치하는 ReadStatus 존재 유무 확인
        return readStatusListWithChannelId.stream()
                .anyMatch(readStatus -> readStatus.getUserId().equals(userId));
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        // 1. channelId에 해댕하는 ReadStatus 리스트 가져오기
        List<ReadStatus> readStatusListWithChannelId = readStatusMapWithChannelId.getOrDefault(channelId, new ArrayList<>());

        // 2. userIdd와 일치하는 ReadStatus filter 하여 반환
        return (ReadStatus) readStatusListWithChannelId.stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId)); // userId와 일치하는 ReadStatus만 필터링
    }

    @Override
    public ReadStatus update(ReadStatus readStatus) {

        return readStatusMap.put(readStatus.getId(), readStatus);
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        if (readStatusMapWithUserId.containsKey(userId)) {
            List<ReadStatus> result = readStatusMapWithUserId.getOrDefault(userId, new ArrayList<>());

            return result;
        } else {
            throw new NoSuchElementException("ReadStatus not found");
        }
    }

    @Override
    public void delete(UUID id) {
        readStatusMap.remove(id);
    }
}

package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entitiy.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;


@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "JCF",matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {

    private final CopyOnWriteArrayList<ReadStatus> data = new CopyOnWriteArrayList<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.add(readStatus);
        return readStatus;
    }

    @Override
    public List<ReadStatus> read() {
        return data;
    }

    @Override
    public Optional<ReadStatus> readById(UUID id) {
        Optional<ReadStatus> readStatus = data.stream()
                .filter((u)->u.getId().equals(id))
                .findAny();
        return readStatus;
    }

    @Override
    public List<ReadStatus> readByUserId(UUID userId) {
        List<ReadStatus> findByUserIdList = data.stream()
                .filter(user->user.getUserId().equals(userId))
                .toList();
        return findByUserIdList;
    }

    @Override
    public List<ReadStatus> readByChannelId(UUID channelId) {
        List<ReadStatus> findByChannelIdList = data.stream()
                .filter(user->user.getChannelId().equals(channelId))
                .toList();
        return findByChannelIdList;
    }

    @Override
    public void update(UUID id, ReadStatus readStatus) {
        data.stream()
                .filter((c)->c.getId().equals(id))
                .forEach((c)->{
                    c.setUpdatedAt(Instant.now());
                });
    }

    @Override
    public void delete(UUID readStatusId) {
        data.removeIf(readStatus -> readStatus.getId().equals(readStatusId));
    }
}

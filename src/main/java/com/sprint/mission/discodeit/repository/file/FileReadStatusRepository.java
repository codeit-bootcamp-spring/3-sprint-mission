package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.util.FilePathProperties;
import com.sprint.mission.discodeit.util.FileSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.repository.file
 * fileName       : FileReadStatusRepository
 * author         : doungukkim
 * date           : 2025. 4. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 26.        doungukkim       최초 생성
 */

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
@RequiredArgsConstructor
public class FileReadStatusRepository implements ReadStatusRepository {
    private final FilePathProperties filePathUtil;


    @Override
    public void updateUpdatedTime(UUID readStatusId, Instant newTime) {
        Path path = filePathUtil.getReadStatusFilePath(readStatusId);
        if (!Files.exists(path)) {
            return;
        }

        ReadStatus readStatus = FileSerializer.readFile(path, ReadStatus.class);
        readStatus.setUpdatedAt(newTime);
        FileSerializer.writeFile(path, readStatus);
    }

    @Override
    public ReadStatus findById(UUID readStatusId) {
        Path path = filePathUtil.getReadStatusFilePath(readStatusId);
        if (!Files.exists(path)) {
            return null;
        }
        return FileSerializer.readFile(path, ReadStatus.class);
    }

    // working..
    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        Path directory = filePathUtil.getReadStatusDirectory();

        if (!Files.exists(directory)) {
            return Collections.emptyList();
        }

        List<ReadStatus> readStatusList;
        try {
            readStatusList = Files.list(directory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (ReadStatus) data;
                        } catch (IOException | ClassNotFoundException exception) {
                            throw new RuntimeException("파일을 읽어오지 못했습니다: FileReadStatusRepository.findReadStatusByChannelId", exception);
                        }
                    })
                    .filter(readStatus -> readStatus.getUserId().equals(userId))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("리스트로 만드는 과정에 문제 발생: FileReadStatusRepository.findReadStatusByChannelId",e);
        }

        return readStatusList;
    }

    @Override
    public void deleteReadStatusById(UUID readStatusId) {
        Path path = filePathUtil.getReadStatusFilePath(readStatusId);

        try{
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("ReadStats 삭제 실패: FileReadStatusRepository.deleteReadStatusById",e);
        }

    }

    @Override
    public ReadStatus createByUserId(UUID userId, UUID channelId) {
        ReadStatus readStatus = new ReadStatus(userId, channelId);
        Path path = filePathUtil.getReadStatusFilePath(readStatus.getId());
        FileSerializer.writeFile(path, readStatus);
        return readStatus;
    }

    @Override
    public List<ReadStatus> createByUserId(List<UUID> userIds, UUID channelId) {
        List<ReadStatus> readStatusList = new ArrayList<>();
        for (UUID userId : userIds) {
            ReadStatus readStatus = new ReadStatus(userId, channelId);
            Path path = filePathUtil.getReadStatusFilePath(readStatus.getId());
            FileSerializer.writeFile(path, readStatus);
            readStatusList.add(readStatus);
        }
        return readStatusList;
    }

    @Override
    public List<ReadStatus> findReadStatusesByChannelId(UUID channelId) {
        Path directory = filePathUtil.getReadStatusDirectory();

        if (!Files.exists(directory)) {
            return Collections.emptyList();
        }

        List<ReadStatus> readStatusList;
        try {
            readStatusList = Files.list(directory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (ReadStatus) data;
                        } catch (IOException | ClassNotFoundException exception) {
                            throw new RuntimeException("파일을 읽어오지 못했습니다: FileReadStatusRepository.findReadStatusByChannelId", exception);
                        }
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException("리스트로 만드는 과정에 문제 발생: FileReadStatusRepository.findReadStatusByChannelId",e);
        }

        List<ReadStatus> selectedReadStatuses = new ArrayList<>();
        for (ReadStatus readStatus : readStatusList) {
            if (readStatus.getChannelId().equals(channelId)) {
                selectedReadStatuses.add(readStatus);
            }
        }

        return selectedReadStatuses;
    }
}

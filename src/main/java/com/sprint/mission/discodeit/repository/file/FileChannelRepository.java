package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.helper.FilePathProperties;
import com.sprint.mission.discodeit.helper.FileSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository.file
 * fileName       : FileChannelRepository2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */


@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
@RequiredArgsConstructor
public class FileChannelRepository implements ChannelRepository {
    private final FilePathProperties filePathUtil;


    @Override
    public Channel createPrivateChannelByName() {
        Channel channel = new Channel();
        Path path = filePathUtil.getChannelFilePath(channel.getId());
        FileSerializer.writeFile(path,channel);
        return channel;
    }

    @Override
    public Channel createPublicChannelByName(String name, String description) {
        Channel channel = new Channel(name, description);
        Path path = filePathUtil.getChannelFilePath(channel.getId());
        FileSerializer.writeFile(path,channel);
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        if (!Files.exists(path)) {
            return null;
        }
        return FileSerializer.readFile(path, Channel.class);
    }

    @Override
    public List<Channel> findAllChannel() {

        Path directory = filePathUtil.getChannelDirectory();

        if (!Files.exists(directory)) {
            return Collections.emptyList();
        }

        try {
            return Files.list(directory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (Channel) data;
                        } catch (IOException | ClassNotFoundException exception) {
                            throw new RuntimeException("파일을 읽어오지 못했습니다: FileChannelRepository.findAllChannel", exception);
                        }
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException("채널들을 리스트로 만드는 과정에 문제 발생: FileChannelRepository.findAllChannel",e);
        }

    }

    @Override
    public void updateChannel(UUID channelId, String name) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        if (!Files.exists(path)) {
            throw new RuntimeException("파일 없음: FileChannelRepository.updateChannel");
        }
        Channel channel = FileSerializer.readFile(path, Channel.class);
        channel.setName(name);
        FileSerializer.writeFile(path, channel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Path path = filePathUtil.getChannelFilePath(channelId);

        if (!Files.exists(path)) {
            throw new IllegalStateException("삭제할 파일 없음");
        }

        try{
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("삭제중 오류 발생: FileChannelRepository.deleteChannel", e);
        }
    }
}

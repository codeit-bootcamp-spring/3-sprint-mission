package com.sprint.mission.discodeit.service.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

public class FileChannelService implements ChannelService {

    private final Path dataDirectory;
    private final UserService userService;
    private final Object lock = new Object();

    public FileChannelService(UserService userService) {
        this.dataDirectory = Paths.get(System.getProperty("user.dir"), "data", "channels");
        this.userService = userService;
        init();
    }

    private void init() { // 사용자 데이터 디렉토리 초기화
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                throw new RuntimeException("사용자 데이터 디렉토리 생성 실패", e);
            }
        }
    }

    private Path getChannelPath(UUID channelId) {
        return dataDirectory.resolve(channelId.toString() + ".ser");
    }

    private void saveChannel(Channel channel) {
        Path channelPath = getChannelPath(channel.getChannelId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(channelPath.toFile()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 실패: " + channel.getChannelId(), e);
        }
    }

    private Channel loadChannel(Path path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 로드 실패: " + path, e);
        }
    }

    /**
     * 채널 생성
     *
     * lock 객체를 사용해 동기화 처리: 여러 스레드가 동시에 채널 생성 시 데이터 일관성 보장, 손상 방지
     *
     * @param channelName 채널 이름
     * @param isPrivate 채널 공개 여부
     * @param password 채널 비밀번호
     * @param channelOwnerId 채널 소유자 ID
     */
    @Override
    public Channel createChannel(String channelName, boolean isPrivate, String password, UUID channelOwnerId) {
        synchronized (lock) {
            if (userService.getUserById(channelOwnerId) == null) {
                throw new IllegalArgumentException("존재하지 않는 사용자 ID로는 채널을 생성할 수 없습니다.");
            }
            Channel channel = new Channel(channelName, isPrivate, password, channelOwnerId);
            channel.addParticipant(channelOwnerId);
            saveChannel(channel);
            return channel;
        }
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        Path channelPath = getChannelPath(channelId);
        if (Files.exists(channelPath)) {
            return loadChannel(channelPath);
        }
        return null;
    }

    @Override
    public List<Channel> getAllChannels() {
        try {
            return Files.list(dataDirectory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadChannel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("채널 목록 로드 실패", e);
        }
    }

    @Override
    public Set<UUID> getChannelParticipants(UUID channelId) {
        Channel channel = getChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널 ID입니다.");
        }
        return channel.getParticipants();
    }

    @Override
    public boolean joinChannel(UUID channelId, UUID userId, String password) {
        Channel channel = getChannelById(channelId);
        // 채널 존재 여부 확인
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        // 참가 사용자 존재 여부 확인
        if (userService.getUserById(userId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        // 이미 참가한 사용자인지 확인
        if (channel.isParticipant(userId)) {
            return false;
        }
        // 비공개 채널인 경우 비밀번호 확인
        if (channel.isPrivate() && !channel.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // 참가자 추가
        channel.addParticipant(userId);

        saveChannel(channel);
        return true;
    }

    /**
     * 채널 업데이트
     *
     * lock 객체를 사용해 동기화 처리: 여러 스레드가 동시에 채널 업데이트 시 데이터 일관성 보장. 한 스레드가 채널을 수정하는 동안
     * 다른 스레드가 같은 채널을 수정하지 못하도록 보장.
     *
     * @param channelId 채널 ID
     * @param channelName 채널 이름
     * @param isPrivate 채널 공개 여부
     * @param password 채널 비밀번호
     */
    @Override
    public void updateChannel(UUID channelId, String channelName, boolean isPrivate, String password) {
        synchronized (lock) {
            Channel channel = getChannelById(channelId);
            if (channel == null) {
                throw new IllegalArgumentException("존재하지 않는 채널 ID입니다.");
            }

            if (channelName != null && !channelName.isEmpty()) {
                channel.updateChannelName(channelName);
            }
            channel.updatePrivate(isPrivate);
            if (password != null && !password.isEmpty()) {
                channel.updatePassword(password);
            }

            saveChannel(channel);
        }
    }

    @Override
    public boolean leaveChannel(UUID channelId, UUID userId) {
        Channel channel = getChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널 ID입니다.");
        }

        // 채널 소유자는 나갈 수 없도록 체크
        if (channel.getOwnerChannelId().equals(userId)) {
            throw new IllegalArgumentException("채널 소유자는 채널을 나갈 수 없습니다.");
        }

        boolean left = channel.removeParticipant(userId);
        if (left) {
            saveChannel(channel);
        }
        return left;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        try {
            Files.deleteIfExists(getChannelPath(channelId));
        } catch (IOException e) {
            throw new RuntimeException("채널 삭제 실패: " + channelId, e);
        }
    }
}

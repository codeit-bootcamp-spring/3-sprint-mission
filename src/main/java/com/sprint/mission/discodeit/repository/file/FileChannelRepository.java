package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private final File file = new File("/data/channels.txt");

    public void save(Channel channel) { // entity를 받고 return 없음

        // 부모 디렉토리 없으면 생성
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // data/ 폴더 생성
        }

        try (ObjectOutputStream objectOOS = new ObjectOutputStream(new FileOutputStream(file)) // FOS는 Path를 직접 파라미터로 받지 못함
        ) {

            objectOOS.writeObject(channel);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save(List<Channel> channelList) {
        // 부모 디렉토리 없으면 생성
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // data/ 폴더 생성
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {

            oos.writeObject(channelList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Channel> load() { // List 출력
        List<Channel> objectList = new ArrayList();

        Path path = Paths.get("/data/channels.txt");
        try (ObjectInputStream objectOIS = new ObjectInputStream(new FileInputStream(path.toFile()))
        ) {
            if (!Files.exists(path)) {
                return new ArrayList<>();
            }
            for (Channel object :(List<Channel>) objectOIS.readObject()) {

                objectList.add(object);
            }
        } catch (FileNotFoundException e) {
            System.out.println("파일을 찾을 수 없습니다."); // 비즈니스 로직
            throw new RuntimeException(e);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("파일에 아무 것도 없습니다."); // 비즈니스 로직
            throw new RuntimeException(e);
        }
        return objectList;
    }

    @Override
    public void create(Channel channel) {

        save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        // 1. id를 입력하면, id에 해당하는 유저를 역직렬화하여 불러온다.
        List<Channel> objectList = load();
        for (Channel object : objectList) {
            if (object instanceof Channel) {
                Channel channel = (Channel) object;
                if (channel.getId().equals(id)) {
                    return channel;
                }
            }
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {

        List<Channel> channel = load();
        return channel;
    }

    @Override
    public void update(UUID id, String newName, String newDescription) {
        List<Channel> channelList = load(); // 기존 채널 리스트 로드

        for (Channel channel : channelList) {
            if (channel.getId().equals(id)) {
                channel.updateChannel(newName, newDescription); // 채널 정보 수정
                break;
            }
        }

        save(channelList); // 전체 리스트를 다시 저장
    }

    @Override
    public void delete(UUID id) {
        List<Channel> objectList = load();
        for (Channel object : objectList) {
            if (object.getId().equals(id)) {
                objectList.remove(object);
                save(objectList);
                break;
            }
        }
    }
}

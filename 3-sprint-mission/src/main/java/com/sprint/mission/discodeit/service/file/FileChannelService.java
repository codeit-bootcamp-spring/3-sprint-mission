package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.entity.Channel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static final Path directory = Paths.get(System.getProperty("user.dir"),"data");
    private static final Path filepath = Paths.get(String.valueOf(directory), "channels.ser");
    private List<Channel> data;

    public FileChannelService() throws IOException {
        init(directory);
        this.data = load(filepath);
    }

    // 저장할 경로의 파일 초기화
    public static void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> void save(T data) {
        try(
                FileOutputStream fos = new FileOutputStream(filepath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Channel> load(Path filepath) throws IOException {
        if (!Files.exists(filepath)) {
            return new ArrayList<>();
        }

        try (
                FileInputStream fis = new FileInputStream(filepath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object data = ois.readObject();
            return (List<Channel>) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 로딩 실패", e);
        }
    }

    @Override
    public Channel create(User currentUser) throws IOException {

        System.out.println("새로 만들 채팅방 이름을 입력하십시오.");
        String name = reader.readLine();

        Channel channel = new Channel(currentUser.getId(), name);

        this.data.add(channel);
        System.out.println("채팅방이 생성되었습니다.");

        save(this.data);
        return channel;
    }

    @Override
    public Channel find(User currentUser, UUID id){

        return findAll(currentUser).stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 없습니다."));
    }

    @Override
    public List<Channel> findByName(User currentUser, String name) {

        return findAll(currentUser).stream()
                .filter(c -> c.getName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> findAll(User currentUser) {

        return this.data.stream()
                .filter(c -> c.getEntry().contains(currentUser.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateName(User currentUser, UUID id) throws IOException {
        Channel channel = find(currentUser, id);

        System.out.println("새로운 이름을 입력하십시오.");
        String newname = reader.readLine();

        channel.updateById(id, newname);
        System.out.println("수정되었습니다.");

        save(this.data);
    }

    @Override
    public void delete(User currentUser, UUID id) {
        Channel channel = find(currentUser, id);

        if (currentUser.getId().equals(channel.getMaker())) {

            this.data.removeIf(c -> c.getId().equals(id));
            System.out.println("채팅방이 삭제되었습니다.");

            save(this.data);

        } else {
            System.out.println("삭제 권한이 없습니다.");
        }
    }

    @Override
    public void addEntry(User currentUser, UUID id, UUID entryId) {
        Channel channel = find(currentUser, id);

        if (!channel.getEntry().contains(entryId)) {
            channel.addEntry(entryId);
            System.out.println("추가되었습니다.");

            save(this.data);
        } else {
            System.out.println("이미 추가된 사용자입니다.");
        }

        System.out.println("-----{"+channel.getName()+"} 참가자 목록-----");

        System.out.println(channel.getEntry().toString());

        System.out.println("----------------------");
    }

    @Override
    public Channel enterChannel(User currentUser, UUID id) throws IOException{
        List<Channel> channels = findAll(currentUser);

        if (channels.stream().anyMatch(c -> c.getId().equals(id))) {

            Channel channel = find(currentUser, id);
            System.out.println(channel.getName() + "에 입장!");

            return channel;
        } else {
            System.out.println("해당 채팅방이 없습니다.");
            return null;
        }
    }
}

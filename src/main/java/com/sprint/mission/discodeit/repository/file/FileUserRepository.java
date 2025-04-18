package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private static final Path FILE_PATH = Paths.get("data/user.ser");
    private final Map<UUID, User> data;

    public FileUserRepository() {
        this.data = readAll();
    }

    public FileUserRepository(Map<UUID, User> data) {
        this.data = data;
    }

    /**
     * 유저 데이터를 직렬화하여 파일에 저장하는 메서드
     *
     * @throws RuntimeException
     */
    @Override
    public void saveAll() {
        try{
            Files.createDirectories(FILE_PATH.getParent());
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH.toFile()))){
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException("메시지 데이터 파일을 저장하는 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 유저를 파일에 저장하는 메서드
     *
     * @param user 저장할 유저
     */
    @Override
    public void save(User user) {
        data.put(user.getId(), user);
        saveAll();
    }

    /**
     * 주어진 id에 해당하는 유저를 삭제하는 메서드
     *
     * @param id 삭제할 대상 유저 id
     */
    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveAll();
    }

    /**
     * 파일에서 읽어온 유저 데이터를 역직렬화하여 로드하는 메서드
     *
     * @return Map<UUID, User> 형태
     * @throws RuntimeException
     */
    @SuppressWarnings("unchecked")
    public Map<UUID, User> readAll() {
        if (!Files.exists(FILE_PATH)) {
            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH.toFile()))
        ) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저 데이터 파일을 읽는 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 주어진 id에 해당하는 유저를 조회하는 메서드
     *
     * @param id 조회할 유저의 id
     * @return 조회된 유저
     */
    @Override
    public Optional<User> readById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    /**
     * 주어진 유저ID에 해당하는 유저를 조회하는 메서드
     *
     * @param userId 조회할 유저ID
     * @return 조회된 유저
     */
    @Override
    public Optional<User> readByUserId(String userId) {
        return data.values().stream()
                .filter(u->u.getUserId().equals(userId))
                .findFirst();
    }
}

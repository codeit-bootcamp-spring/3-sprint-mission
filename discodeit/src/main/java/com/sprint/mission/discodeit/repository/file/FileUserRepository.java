package com.sprint.mission.discodeit.repository.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.FileioUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Repository
public class FileUserRepository implements UserRepository {
    private Map<String, User> userData;
    private Path path;
    private ObjectMapper objectMapper;

    public FileUserRepository(@Qualifier("userFilePath") Path path) {
        this.path = path;

        // ObjectMapper 초기화 및 JavaTimeModule 등록
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());  // JavaTimeModule을 등록하여 Instant 처리

        if (!Files.exists(this.path)) {
            try {
                Files.createFile(this.path);
                FileioUtil.save(this.path, new HashMap<>());
            } catch (IOException e) {
                throw new RuntimeException("[error] User 파일을 초기화 불가능", e);
            }
        }
        FileioUtil.init(this.path);
        this.userData = FileioUtil.load(this.path, User.class);  // 변경된 메서드로 파일 데이터 로딩
    }


    @Override
    public User save(User user) {
        userData.put(user.getId().toString(), user);
        FileioUtil.save(this.path, userData);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        if (!userData.containsKey(id.toString())) {
            throw new NoSuchElementException("User not found with id " + id);
        }
        return Optional.ofNullable(userData.get(id.toString()));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userData.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return userData.values().stream().toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return userData.containsKey(id.toString());
    }

    @Override
    public void deleteById(UUID id) {
        userData.remove(id.toString());
        FileioUtil.save(this.path, userData);
    }
}

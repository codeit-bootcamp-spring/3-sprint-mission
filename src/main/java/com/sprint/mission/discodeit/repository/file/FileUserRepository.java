package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository("fileUserRepository")
public class FileUserRepository implements UserRepository {
    private static final String DIR = "data/users/";

    public FileUserRepository() { clearFile(); }

    @Override
    public void save(User user) {
        try (
            FileOutputStream fos = new FileOutputStream(DIR + user.getId() + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User loadByName(String name) {
        List<User> users = loadAll();

        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User loadByEmail(String email) {
        List<User> users = loadAll();

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User loadById(UUID id) {
        File file = new File(DIR + id + ".ser");
        if (!file.exists()) {
            throw new IllegalArgumentException("[User] 유효하지 않은 user 파일 (" + id + ".ser)");
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (User) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[User] 사용자 로드 중 오류 발생", e);
        }
    }

    @Override
    public List<User> loadAll() {
        if (Files.exists(Path.of(DIR))) {
            try {
                List<User> users = Files.list(Paths.get(DIR))
                        .map( path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Object data = ois.readObject();
                                return (User) data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException("[User] 파일 로드 중 오류 발생", e);
                            }
                        })
                        .toList();
                return users;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            File file = new File(DIR + id + ".ser");
            if (!file.delete()) { //file.delete는 실패했을 때 예외 반환 x
                System.out.println("[User] 파일 삭제 실패");
            }
        } catch (Exception e) {
            throw new RuntimeException("[User] 파일 접근 오류 (" + id + ")", e);
        }
    }

    /**
     *  프로그램 시작 시
     *  users 폴더 초기화
     *  users 폴더가 비어있으면 초기화 안 하고 메소드 종료
     */
    private void clearFile() {
        File dir = new File(DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) { return; }

            for (File file : files) {
                try {
                    file.delete();
                } catch (Exception e) {
                    throw new RuntimeException("[User] users 폴더 초기화 실패", e);
                }
            }
        }
    }
}
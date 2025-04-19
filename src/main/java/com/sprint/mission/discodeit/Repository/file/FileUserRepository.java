package com.sprint.mission.discodeit.Repository.file;

import com.sprint.mission.discodeit.Repository.UserRepository;
import com.sprint.mission.discodeit.entity.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private static final String DIR = "data/users/";

    public FileUserRepository() {
        clearFile();
    }

    /**
     * user의 id 값으로 {id}.ser 파일 생성
     *
     * @param user
     */
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
    public void update(UUID id, String name) {
        User user = loadById(id);
        if (user == null) {
            throw new IllegalArgumentException("[User] 유효하지 않은 사용자입니다. (" + id + ")");
        }

        user.setName(name);
        save(user);
    }

    @Override
    public void deleteById(UUID id) {
        File file = new File(DIR + id + ".ser");

        try {
            if (file.exists()) {
                if (!file.delete()) { //file.delete는 실패했을 때 예외 반환 x
                    System.out.println("[User] 파일 삭제 실패");
                };
            }
            else {
                System.out.println("[User] 유효하지 않은 파일 (" + id + ")");
            }
        } catch (Exception e) {
            throw new RuntimeException("[User] 파일 삭제 중 오류 발생 (" + id + ")", e);
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

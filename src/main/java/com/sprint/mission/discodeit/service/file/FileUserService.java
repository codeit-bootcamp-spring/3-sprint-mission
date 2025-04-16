package com.sprint.mission.discodeit.service.file;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

//File IO를 통한 데이터 영속화
//[ ]  다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
//
//[ ]  클래스 패키지명: com.sprint.mission.discodeit.service.file
//
//[ ]  클래스 네이밍 규칙: File[인터페이스 이름]
//
//        [ ]  JCF 대신 FileIO와 객체 직렬화를 활용해 메소드를 구현하세요. // 이게 JCF의 기능도 겸하는 걸 만들라는 거지?
//
//객체 직렬화/역직렬화 가이드
//
//[ ]  Application에서 서비스 구현체를 File*Service로 바꾸어 테스트해보세요.

public class FileUserService implements UserService {
    private static final String FILE_PATH = "src/main/java/com/sprint/mission/discodeit/service/file/data/user.txt";
    private final Path path = Paths.get(FILE_PATH);

    private Map<UUID, User> data;

    public FileUserService() {
        this.data = new HashMap<>();
    }

    // 직렬화 : 생성
    public void saveUsers(List<User> users) { // 객체 직렬화
        try ( // 길 뚫어주고
              FileOutputStream userFOS = new FileOutputStream(FILE_PATH); // file 주소를 어떻게 설정할까
              ObjectOutputStream userOOS = new ObjectOutputStream(userFOS);
              // ObjectOutputStream userOOS = new ObjectOutputStream(new FileOutputStream(new File("user.ser")));
        ) {
            userOOS.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 역직렬화 : 조회
    public List<User> loadUsers(Path path) {
        List<User> users = new ArrayList<>();

        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

            try (
                    ObjectInputStream userOIS = new ObjectInputStream(new FileInputStream(FILE_PATH));
            ) {
                for (User user : (List<User>) userOIS.readObject()) {
                    users.add(user);
                }

                return users;
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
    }


    @Override
    public User createUser(String RRN, String name, int age, String email) {
        User user = new User(RRN, name, age, email); // 파라미터로 받아온 user의 정보로 객체 직접 생성
        this.data.put(user.getId(), user);

        List<User> users = new ArrayList<>(data.values());

        saveUsers(users);

        return user;
    }

    @Override
    public User readUser(UUID id) { // R
        List<User> users = loadUsers(path); // 유저 리스트 역직렬화 // 객체 하나하나 역직렬화 아님

        for (User user : users) {
            if (user.getId().equals(id)) {
                System.out.println("유저 정보 출력");
                System.out.println("===================");
                System.out.println("이름: " + user.getName());
                System.out.println("주민번호: " + user.getRRN());
                System.out.println("나이: " + user.getAge());
                System.out.println("이메일: " + user.getEmail());
                return user;
            }
        }

        System.out.println("해당 ID를 가진 유저를 찾을 수 없습니다.");
        return null; // 없으면 null, 예외 처리는 아직 못했음
    }

    @Override
    public List<User> readAllUsers() { // R 전체 조회
        return loadUsers(path);
    }

    @Override
    public User updateUser(UUID id, String newName, String newEmail) { // U
        User userNullable = this.data.get(id);
        User user = Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException(id + "ID를 가진 사용자를 찾을 수 없습니다."));
        user.updateUser(newName, newEmail);

        saveUsers(this.data.values().stream().toList());

        return user;
    }

    @Override
    public void deleteUser(UUID id) { // D
        if (!this.data.containsKey(id)) {
            throw new NoSuchElementException(id + "ID를 가진 사용자를 찾을 수 없습니다.");
        }
        this.data.remove(id);
        saveUsers(this.data.values().stream().toList());
    };
}

package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileUserService implements UserService {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static final Path directory = Paths.get(System.getProperty("user.dir"),"data");
    private static final Path filepath = Paths.get(String.valueOf(directory), "users.ser");
    private List<User> data;

    public FileUserService() throws IOException {
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

    public static List<User> load(Path filepath) throws IOException {
        if (!Files.exists(filepath)) {
            return new ArrayList<>();
        }

        try (
                FileInputStream fis = new FileInputStream(filepath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object data = ois.readObject();
            return (List<User>) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 로딩 실패", e);
        }
    }

    @Override
    public User create() throws IOException {
        while (true) {
            System.out.println("ID: ");
            String userid = reader.readLine();

            // 아이디 중복체크
            boolean isDuplicated = this.data.stream()
                    .anyMatch(u -> u.getUserId().equals(userid));
            if (isDuplicated) {
                System.out.println("이미 존재하는 아이디입니다.");
            } else {

                System.out.println("Password: ");
                String password = reader.readLine();

                System.out.println("Name: ");
                String name = reader.readLine();

                User newUser = new User(userid, password, name);
                this.data.add(newUser);
                System.out.println("사용자 등록 완료: " + userid);

                save(this.data);
                return newUser;
            }
        }
    }

    @Override
    public User find(UUID id) {
        return this.data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public User findByUserId(String userid) {
        return this.data.stream()
                .filter(u -> u.getUserId().equals(userid))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    public List<User> findByName(String name) {
        return this.data.stream()
                .filter(u -> u.getName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAll() {
        return this.data;
    }

    @Override
    public User update(UUID id) throws IOException {
        User user = this.data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        if (user.getIsLogin()) {
            System.out.println("사용자 정보 수정");
            System.out.println("1: 이름");
            System.out.println("2: 비밀번호");
            System.out.println("=======================");
            System.out.println("수정할 정보의 번호를 입력하세요: ");

            String num = String.valueOf(reader.readLine());
            if (num.equals("1")) {
                System.out.println("새로운 정보 입력");
                String name = reader.readLine();
                user.updateName(name);
            } else if (num.equals("2")) {
                System.out.println("새로운 정보 입력");
                String password = reader.readLine();
                user.updatePassword(password);
            } else {
                System.out.println("유효한 값을 입력하세요.");
            }

        } else {
            System.out.println("먼저 로그인하십시오.");
        }

        save(this.data);
        return user;
    }

    @Override
    public void delete(UUID id) {
        this.data.removeIf(u -> u.getId().equals(id));
        System.out.println("삭제되었습니다.");
        save(this.data);
    }

    @Override
    public User login() throws IOException {
        System.out.println("ID: ");
        String userid = reader.readLine();

        System.out.println("Password: ");
        String password = reader.readLine();

        User user = this.data.stream()
                .filter(u -> u.getUserId().equals(userid) && u.getPassword().equals(password))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        user.setLogin();
        System.out.println("로그인 성공!");

        return user;
    }

    @Override
    public void logout(User user) {

        if (user.getIsLogin()) {
            user.setLogout();
            System.out.println("로그아웃 되었습니다.");
        } else {
            System.out.println("이미 로그아웃 되었습니다..");
        }
    }

}

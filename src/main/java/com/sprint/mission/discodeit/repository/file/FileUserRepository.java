package com.sprint.mission.discodeit.repository.file;

//[ ] 다음의 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요.
//[ ] 클래스 패키지명: com.sprint.mission.discodeit.repository.file
//[ ] 클래스 네이밍 규칙: File[인터페이스 이름]
//        [ ] 기존에 구현한 File*Service 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUserRepository implements UserRepository {

    private final File file = new File("/data/users.txt");

    // TODO 저장 메소드 (Serialization)
    // 1. Instance 정보를 받는다.
    // // 1-1. 파라미터로 객체를 받는다. or 객체 리스트를 받는다
    // // 1-2. 저장 목록을 리턴?
    // 2. Instance 정보를 파일(경로)에 저장한다.
    // 3. 저장된 Instance의 정보가 담긴 리스트를 리턴한다. <- 굳이 할 필요가 있나?

    public void save(User user) {
        // 부모 디렉토리 없으면 생성
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // data/ 폴더 생성
        }

        try (ObjectOutputStream objectOOS = new ObjectOutputStream(new FileOutputStream(file)) // FOS는 Path를 직접 파라미터로 받지 못함
        ) {
            objectOOS.writeObject(user);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save(List<User> userList) {

        // 부모 디렉토리 없으면 생성
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // data/ 폴더 생성
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {

            oos.writeObject(userList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public List<User> load() {
        List<User> objectList = new ArrayList();

        Path path = Paths.get("/data/users.txt");

        try (ObjectInputStream objectOIS = new ObjectInputStream(new FileInputStream(path.toFile()))
        ) {
            if (!Files.exists(path)) {
                return new ArrayList<>();
            }
            for (User object :(List<User>) objectOIS.readObject()) {

                objectList.add(object);
            }
        } catch (FileNotFoundException e) {
            System.out.println("파일을 찾을 수 없습니다.");
            throw new RuntimeException(e);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("파일에 아무 것도 없습니다.");
            throw new RuntimeException(e);
        }
        return objectList;
    }


    public void create(User user) { // Controller에서 만들어진 User 타입을 파라미터로 씀
        save(user);
    }


    // TODO 읽기 메소드 (Deserialization)
    // // 1. 파일의 경로를 파라미터로 받는다.
    // // 2. 불러온 Instance의 정보가 담긴 리스트를 리턴한다.

    @Override
    public User findById(UUID id) {
        // 1. id를 입력하면, id에 해당하는 유저를 역직렬화하여 불러온다.
        List<User> objectList = load();
        for (User object : objectList) {
            if (object instanceof User) {
                User user = (User) object;
                if (user.getId().equals(id)) {
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return load();
    }

    @Override
    public void update(UUID id, String newName, String newEmail) {
        List<User> objectList = load();
        for (User object : objectList) {
            if (object.getId().equals(id)) {
                object.updateUser(newName, newEmail);
                save(objectList);
                break;
            }
        }
    }

    @Override
    public void delete(UUID id) {
        List<User> objectList = load();
        for (User object : objectList) {
            if (object.getId().equals(id)) {
                objectList.remove(object);
                save(objectList);
                break;
            }
        }
    }
    // TODO 호출 메소드
    // Instance의 정보가 저장된 파일의 주소를 불러온다.
    // // 1. save와 read 메소드가 반환한 List를 파라미터로 받는다.
    // // 2. save와 read 메소드로 저장(호출)된 파일의 경로를 불러온다
    // // 3. String 타입으로 경로를 리턴한다.
//    String loadPath(List<Object> list);
}

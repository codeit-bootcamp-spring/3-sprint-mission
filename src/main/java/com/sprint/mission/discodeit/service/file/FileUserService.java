package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


// 1차 도움 요청 조언
// serialVersionUID는 DTO 즉 channel message user에서 정의해야합니다  << 해결
// 파일 주소도 src/main/java/com... 처럼 작성해줘야해요   << 수정 후 보류( 정확한 경로 지정인지 아직 모르겠음 )

public class FileUserService implements UserService {
    private static final String USER_FILE_PATH = "src/main/java/com/sprint/mission/discodeit/service/data/users.txt";     // 대상 파일 정보를 불변 객체 선언 및 공유화( CRUD 및 load, save 목적 + 코드 간결화 )

    // 사용자 리스트 불러오기
    private List<User> loadUsers() {
        // 사용자 정보 저장 경로 지정? 초기화?
        File file = new File(USER_FILE_PATH);
        if (!file.exists()) {     // 파일이 존재하지 않는다면...    ( 방어 코드 )
            return new ArrayList<>();     // 새로운 ArrayList 반환
        }

        // 객체 역직렬화 ( 저장된 정보를 끌어와야하니까 )
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();     // 예외처리 로그 출력
            return new ArrayList<>();     // 예외 발생 시 빈 리스트 반환
        }
    }



    // 사용자 리스트 저장
    private void saveUser(List<User> users) {

        // 객체 직렬화 ( 객체를 파일에 저장하기에 )
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();     // 예외처리 로그 출력
        }
    }


    @Override
    public void createUser(User user) {
        List<User> users = loadUsers();     // 기존 사용자 정보 불러오기
        users.add(user);     // 사용자 리스트에 추가
        saveUser(users);     // 생성된 사용자 정보 저장
    }

    @Override
    public User readUser(UUID id) {
        return loadUsers().stream()     // 전체 정보 중
                .filter(user -> user.getUserId().equals(id))     // 찾고자 하는 특정 ID의 해당하는 사용자
                .findFirst()     // 그 중 첫번째( 중복 불가하지만 방어적 코드 인용 )
                .orElse(null);     // 없다면 null 처리
    }

    @Override
    public List<User> readUserByName(String name) {
        return loadUsers().stream()     // 전체 정보 중
                .filter(user -> user.getUserName().equalsIgnoreCase(name))     // 유저 이름이 동일한 대상
                .collect(Collectors.toList());     // 리스트로 저장
    }

    @Override
    public List<User> readAllUsers() {
        return loadUsers();     // 사용자 정보 불러오기
    }

    @Override
    public User updateUser(UUID id, User user) {
        List<User> users = loadUsers();     // 사용자 불러오기
        for (int i = 0; i < users.size(); i++) {     // 최대 크기만큼 인덱스를 돌려가며 ID가 일치하는 대상의 정보 수정
            if (users.get(i).getUserId().equals(id)) {
                users.set(i, user);
                saveUser(users);
                return user;
            }
        }
        return null;     // 해당 ID를 가진 사용자 없음
    }

    @Override
    public boolean deleteUser(UUID id) {
        List<User> users = loadUsers();
        // 일치하는 ID의 사용자 삭제 시도( 있다면 True, 없다면 false )
        boolean removed = users.removeIf(user -> user.getUserId().equals(id));
        if (removed) {     // 해당 ID의 유저가 존재하여 삭제 시
            saveUser(users);     // 변경 정보를 사용자 리스트에 저장 ( 삭제 반영 )
        }
        return removed;
    }
}

package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class JCFUserService implements UserService {
    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public final List<User> data;

    public JCFUserService(){
        this.data = new ArrayList<>();
    }
    @Override
    public void create() throws IOException {
        boolean isFinished = false;
        while (!isFinished) {
            System.out.println("ID: ");
            String userid = reader.readLine();

            // 아이디 중복체크
            boolean isDuplicated = this.readAll().stream()
                    .anyMatch(u -> u.getUserId().equals(userid));
            if (isDuplicated) {
                System.out.println("이미 존재하는 아이디입니다.");
            } else {

                System.out.println("Password: ");
                String password = reader.readLine();

                System.out.println("Name: ");
                String name = reader.readLine();

                this.data.add(new User(userid, password, name));
                System.out.println("사용자 등록 완료: " + userid);
                isFinished = true;
                }
            }
        }

    @Override
    public User read(String userid) {
        return this.data.stream()
                .filter(u -> u.getUserId().equals(userid))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    public List<User> readByName(String name) {
        return this.data.stream()
                .filter(u -> u.getName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<User>  readAll() {
        return this.data.stream()
                .collect(Collectors.toList());
    }

    @Override
    public User login() throws IOException {
        for (int i=0; i<4; i++) {
            System.out.println("ID: ");
            String userid = reader.readLine();
            System.out.println("Password: ");
            String password = reader.readLine();

            boolean isCorrect = this.data.stream()
                    .filter(u -> u.getUserId().equals(userid) && u.getPassword().equals(password))
                    .findAny()
                    .isPresent();
            if (isCorrect) {
                System.out.println("로그인 성공!");
                User user = this.data.stream()
                        .filter(u -> u.getUserId().equals(userid) && u.getPassword().equals(password))
                        .findFirst()
                        .orElseThrow(IllegalArgumentException::new);
                user.setLogin();
                return user;
            } else {
                System.out.println("다시 시도해 주십시오.");
            }
        }
        System.out.println("로그인이 차단되었습니다.");
        return null;
    }


    public void update(User user) throws IOException {
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
                user.updateDateTime();
            } else if (num.equals("2")) {
                System.out.println("새로운 정보 입력");
                String password = reader.readLine();
                user.updatePassword(password);
                user.updateDateTime();
            } else {
                System.out.println("유효한 값을 입력하세요.");
            }

        } else {
            System.out.println("먼저 로그인하십시오.");
        }
    }


    @Override
    public void logout(User user) {
        System.out.println(this.read(user.getUserId()).toString());
        if (this.read(user.getUserId()) != null) {
            user.setLogout();
            System.out.println("로그아웃 되었습니다.");
        } else {
            System.out.println("존재하지 않는 사용자입니다.");
        }
    }

    @Override
    public void delete(User user) {
        String userid = user.getUserId();
        this.data.removeIf(u -> u.getId().equals(user.getId()));
        System.out.println("'" + userid + "' 가 삭제되었습니다.");
    }
}

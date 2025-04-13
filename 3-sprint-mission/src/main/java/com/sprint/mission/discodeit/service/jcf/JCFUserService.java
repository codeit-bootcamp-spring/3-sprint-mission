package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import org.w3c.dom.ls.LSOutput;

import java.rmi.NoSuchObjectException;
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
            System.out.println(isDuplicated);
            if (isDuplicated) {
                System.out.println("이미 존재하는 아이디입니다.");
            } else {

                System.out.println("Password: ");
                String password = reader.readLine();

                System.out.println("Name: ");
                String name = reader.readLine();

                // 사용자 정보 유효성 체크
                if (userid.equals(null)
                        || password.equals(null)
                        || name.equals(null)) {
                    System.out.println("유효한 값을 입력하여 주십시오.");
                } else {
                    this.data.add(new User(userid, password, name));
                    System.out.println("사용자 등록 완료: " + userid);
                    isFinished = true;
                }
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
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("ID: ");
            String userid = br.readLine();
            System.out.println("Password: ");
            String password = br.readLine();

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

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String num = String.valueOf(br.readLine());
            if (num.equals("1")) {
                System.out.println("새로운 정보 입력");
                String name = br.readLine();
                user.updateName(name);
                user.updateDateTime();
            } else if (num.equals("2")) {
                System.out.println("새로운 정보 입력");
                String password = br.readLine();
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
        System.out.println("A");
        System.out.println(new JCFUserService().read(user.getUserId()).toString());
        System.out.println("B");
        if (new JCFUserService().read(user.getUserId()) != null) {
            user.setLogout();
            System.out.println("로그아웃 되었습니다.");
        } else {
            System.out.println("존재하지 않는 사용자입니다.");
        }
    }

    @Override
    public void delete(User user) {
        if (user.getIsLogin()) {
            this.data.removeIf(u -> u.getId().equals(user.getId()));
        } else {
            System.out.println("먼저 로그인하세요.");
        }
    }

//    public void findPassWord(User user, String email) {
//        try {
//            if (email==user.getEmail()) {
//                System.out.println(user.getPassWord());
//            } else {
//                System.out.println("잘못된 이메일 주소입니다.");
//            }
//        } catch (Exception e) {
//            System.out.println("사용자가 존재하지 않습니다.");
//        }
//    }
}

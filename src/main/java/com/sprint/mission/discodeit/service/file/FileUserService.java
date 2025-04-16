package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUserService implements UserService {

    private List<User> users;
    private final String FILEPATH = "users.ser";
    private final Scanner sc = new Scanner(System.in);

    public FileUserService() {
        users = loadUsers();
        if (users == null) {
            users = new ArrayList<>();
        } else {
            int max = 0;
            for (User user : users) {
                if (user.getNumber() > max) {
                    max = user.getNumber();
                }
            }
            User.setCounter(max + 1);
        }
    }


    @SuppressWarnings("unchecked") //실행은 되는데 무슨 에러떠서 지피티한테 물어보고 추가했습니다 !!
    private List<User> loadUsers() {
        File file = new File(FILEPATH);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILEPATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User inputUserName() {
        System.out.println("안녕하세요. DisCodeit에 오신 걸 환영합니다. 사용자의 이름을 입력 해 주세요.");
        String userName = sc.nextLine();
        User newUser = new User(userName);
        users.add(newUser);
        saveUsers();
        return newUser;
    }

    @Override
    public void createNewUserNames(String oldName, String newName) {
        if (oldName.equals(newName.trim())) {
            System.out.println("동일한 프로필은 생성할 수 없습니다.");
        } else {
            users.add(new User(newName));
            saveUsers();
        }
    }

    @Override
    public void outputAllUsersInfo() {
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Override
    public void outputOneUserInfo(String name) {
        List<User> userName = users
                .stream()
                .filter(e -> e.getUsername().equals(name))
                .toList();
        System.out.println(userName);
    }

    @Override
    public void updateUserName(User user, String newName) {
        if (user.getUsername().equals(newName.trim())) {
            System.out.println("프로필 이름은 중복 될 수 없습니다.");
        } else {
            users.stream()
                    .filter(user1 -> user1.getUsername().equals(user.getUsername()))
                    .findFirst()
                    .ifPresent(user1 -> user1.updateUserName(newName));
            saveUsers();
        }
    }

    @Override
    public void deleteUserName(int userNumber) {
        users.stream()
                .filter(user -> user.getNumber() == userNumber)
                .findFirst()
                .ifPresent(user -> users.remove(user));
        saveUsers();
    }

    @Override
    public User changeUser(int userNumber) {
        return users.stream()
                .filter(user1 -> user1.getNumber() == userNumber)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void login(int loginNumber, List<User> usersList) {
        users.stream()
                .filter(u -> u.getNumber() == loginNumber)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 번호의 유저가 존재하지 않습니다: "));
    }
}
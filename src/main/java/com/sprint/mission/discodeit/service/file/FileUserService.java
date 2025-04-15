//package com.sprint.mission.discodeit.service.file;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.io.*;
//import java.util.*;
//
////File IO를 통한 데이터 영속화
////[ ]  다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
////
////[ ]  클래스 패키지명: com.sprint.mission.discodeit.service.file
////
////[ ]  클래스 네이밍 규칙: File[인터페이스 이름]
////
////        [ ]  JCF 대신 FileIO와 객체 직렬화를 활용해 메소드를 구현하세요. // 이게 JCF의 기능도 겸하는 걸 만들라는 거지?
////
////객체 직렬화/역직렬화 가이드
////
////[ ]  Application에서 서비스 구현체를 File*Service로 바꾸어 테스트해보세요.
//
//public abstract class FileUserService implements UserService {
//    private static final String FILE_PATH = "src/main/java/com/sprint/mission/discodeit/service/file/data/user.txt";
//
//    public FileUserService() {}
//
//    // 직렬화 : 생성
//    public void saveUsers(List<User> users) { // 객체 직렬화
//        try ( // 길 뚫어주고
//              FileOutputStream userFOS = new FileOutputStream(FILE_PATH); // file 주소를 어떻게 설정할까
//              ObjectOutputStream userOOS = new ObjectOutputStream(userFOS);
//              // ObjectOutputStream userOOS = new ObjectOutputStream(new FileOutputStream(new File("user.ser")));
//        ) {
//            userOOS.writeObject(users);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 역직렬화 : 조회
//    public List<User> loadUsers() {
//        File file = new File(FILE_PATH);
//        List<User> users = null;
//        if (!file.exists()) {
//            users = new ArrayList<>();
//            return users;
//        }
//
//        try (
////                    FileInputStream userFIS = new FileInputStream(new File("user.ser"));
////                ObjectInputStream userOIS = new ObjectInputStream(userFIS);
//                ObjectInputStream userOIS = new ObjectInputStream(new FileInputStream (FILE_PATH));
//        )  {
//            User userdata = (User) userOIS.readObject();
//            System.out.println("불러온 ID: " + userdata.getId());
//            users.add(userdata);
//            return (List<User>) userOIS.readObject();
//
//            }
////            User userData = (User)userOIS.readObject(); // 역직렬화
////            users.add(userData);
////
//////            Iterator userIterator = users.iterator(); // 근데 얘 이하는 read에다가 따로 만들어줘도 됨. 굳이 여기 만들 필요가? (4/15)
////            System.out.println("유저 정보 출력 \n");
////            System.out.println("===================");
////            while (userIterator.hasNext()) {
////                User u = (User) userIterator.next();
////                System.out.println("이름: " + u.getName());
////                System.out.println("주민번호: " + u.getRRN());
////                System.out.println("나이: " + u.getAge());
////                System.out.println("이메일: " + u.getEmail());
////            }
//            catch (IOException | ClassNotFoundException | ClassCastException e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }
//
//    @Override
//    public User createUser(String RRN, String name, int age, String email) {
//        List<User> users = loadUsers();
//        saveUsers(users);
//    }
//
//    @Override
//    public User readUser(UUID id) { // R // 파라미터로 user1.getId() 넣음
//        List<User> users = loadUsers();
//        for (User user : users) {
//            if (user.getId() != null && user.getId().equals(id)) {
//                return user;
//            }
//        }
//        return null;
////        try {
////            for (User user : users) {
////                if (id.equals(user.getId())) {
////                    return user;
////                }
////            }
////        } catch(NullPointerException e) {
////                e.printStackTrace();
////                System.out.println("유저가 더는 존재하지 않습니다.");
////            }
////
////        return null; // NullPointerException 막야야함
//    }
//}

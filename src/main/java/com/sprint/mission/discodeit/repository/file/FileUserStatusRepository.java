package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.UUID;

@Repository("fileUserStatusRepository")
public class FileUserStatusRepository implements UserStatusRepository {
    private static final String DIR = "data/userStatuses/";

    public FileUserStatusRepository() {
        clearFile();
    }

    @Override
    public void save(UserStatus userStatus) {
        try (
                FileOutputStream fos = new FileOutputStream(DIR + userStatus.getUserId() + ".ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserStatus loadById(UUID id) {
        File file = new File(DIR + id + ".ser");
        if (!file.exists()) {
            throw new IllegalArgumentException("[UserStatus] 유효하지 않은 userStatus 파일 (" + id + ".ser)");
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (UserStatus) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[UserStatus] 로그인 상태 로드 중 오류 발생", e);
        }
    }

    @Override
    public void deleteById(UUID userId) {
        try {
            File file = new File(DIR + userId + ".ser");
            if (!file.delete()) {
                    System.out.println("[UserStatus] 파일 삭제 실패");
            }
        } catch (Exception e) {
            throw new RuntimeException("[UserStatus] 파일 접근 오류 (" + userId + ")", e);
        }
    }

    private void clearFile() {
        File dir = new File(DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) { return; }

            for (File file : files) {
                try {
                    file.delete();
                } catch (Exception e) {
                    throw new RuntimeException("[UserStatus] userStatuses 폴더 초기화 실패", e);
                }
            }
        }
    }
}

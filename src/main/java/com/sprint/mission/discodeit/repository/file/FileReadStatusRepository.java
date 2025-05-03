package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository("fileReadStatusRepository")
public class FileReadStatusRepository implements ReadStatusRepository {
    private static final String DIR = "data/readStatuses/";

    public FileReadStatusRepository() {
        clearFile();
    }

    @Override
    public void save(ReadStatus readStatus) {
        try (
                FileOutputStream fos = new FileOutputStream(DIR + readStatus.getUserId() + ".ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ReadStatus loadById(UUID id) {
        File file = new File(DIR + id + ".ser");
        if (!file.exists()) {
            throw new IllegalArgumentException("[ReadStatus] 유효하지 않은 readStatus 파일 (" + id + ".ser)");
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (ReadStatus) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[ReadStatus] 사용자 로드 중 오류 발생", e);
        }
    }

    public List<ReadStatus> loadAllByUserId(UUID id) {
        File dir = new File(DIR);

        File[] files = dir.listFiles((d, name) -> name.endsWith(".ser"));
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("[ReadStatus] readStatus 파일이 존재하지 않음");
        }

        List<ReadStatus> result = new ArrayList<>();
        for (File file : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                ReadStatus rs = (ReadStatus) ois.readObject();
                if (rs.getUserId().equals(id)) {
                    result.add(rs);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("[ReadStatus] 파일 로드 중 오류 발생: " + file.getName(), e);
            }
        }
        return result;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        try {
            File file = new File(DIR + userId + ".ser");
            if (!file.delete()) {
                System.out.println("[ReadStatus] 파일 삭제 실패");
            }
        } catch (Exception e) {
            throw new RuntimeException("[ReadStatus] 파일 접근 오류 (" + userId + ")", e);
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
                    throw new RuntimeException("[User] readStatuses 폴더 초기화 실패", e);
                }
            }
        }
    }
}

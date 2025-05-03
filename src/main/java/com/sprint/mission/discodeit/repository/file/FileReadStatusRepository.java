package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

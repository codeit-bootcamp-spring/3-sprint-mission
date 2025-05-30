package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.Dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.storage
 * FileName     : BinaryContentStorageManager
 * Author       : dounguk
 * Date         : 2025. 5. 30.
 */
@Service
@RequiredArgsConstructor
public class BinaryContentStorageManager implements BinaryContentStorage {
    private static final String PROFILE_PATH = "img";
    private final JpaBinaryContentRepository binaryContentRepository;
    private final FileUploadUtils fileUploadUtils;

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        String uploadPath = fileUploadUtils.getUploadPath(PROFILE_PATH);
        BinaryContent attachment = binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new IllegalStateException("image information is not saved"));
        String filename = attachment.getFileName();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
//        String newFileName = attachment.getFileName();
        String newFileName = binaryContentId.toString() +"."+ extension;
        File profileImage = new File(uploadPath, newFileName);
        // 사진 저장
        try (FileOutputStream fos = new FileOutputStream(profileImage)) {
            fos.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException("image not saved", e);
        }
        return attachment.getId();
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        String uploadPath = fileUploadUtils.getUploadPath(PROFILE_PATH);
        BinaryContent attachment = binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new IllegalStateException("image information not found"));

        String filename = attachment.getFileName(); // 예: cat.jpg
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        String savedFileName = binaryContentId.toString() + "." + extension;

        File file = new File(uploadPath, savedFileName);
        if (!file.exists()) {
            throw new RuntimeException("file not found: " + file.getAbsolutePath());
        }

        try {
            return new FileInputStream(file); // InputStream 반환
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file cannot be opened", e);
        }
    }

    @Override
    public ResponseEntity<?> download(JpaBinaryContentResponse response) {
        return null;
    }
}

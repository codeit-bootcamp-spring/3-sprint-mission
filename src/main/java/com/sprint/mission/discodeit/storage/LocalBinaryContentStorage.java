package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.Dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.storage
 * FileName     : BinaryContentStorageManager
 * Author       : dounguk
 * Date         : 2025. 5. 30.
 */
@Transactional
@Service
@ConditionalOnProperty(
        name = "discodeit.storage.type",
        havingValue = "local",
        matchIfMissing = false
)
@RequiredArgsConstructor
public class LocalBinaryContentStorage implements BinaryContentStorage {
    private static final String PROFILE_PATH = "img";
    private final JpaBinaryContentRepository binaryContentRepository;
    private final FileUploadUtils fileUploadUtils;

    private Path root;

    @Value("${file.upload.all.path}")
    private String path;


    @PostConstruct
    public void init() {
        String uploadPath = new File(path).getAbsolutePath() + "/" + PROFILE_PATH;
        File directory = new File(uploadPath);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException(uploadPath);
        }
        this.root = Paths.get(uploadPath);
    }


    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
//        String uploadPath = fileUploadUtils.getUploadPath(PROFILE_PATH);
        BinaryContent attachment = binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new IllegalStateException("image information is not saved"));

        String filename = attachment.getFileName();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);

        Path path = resolvePath(binaryContentId, attachment.getExtension());

        // 사진 저장
        try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
            fos.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException("image not saved", e);
        }
        return attachment.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public InputStream get(UUID binaryContentId) {
        BinaryContent attachment = binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new IllegalStateException("image information not found"));

        Path path = resolvePath(binaryContentId, attachment.getExtension());

        if (!Files.exists(path)) {
            throw new RuntimeException("file not found: " + path);
        }

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<?> download(JpaBinaryContentResponse response) {
        try {
            byte[] bytes = get(response.id()).readAllBytes();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(response.contentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+response.fileName()+"\"")
                    .contentLength(response.size())
                    .body(bytes);

        } catch (IOException e) {
            throw new RuntimeException("파일 다운 실패"+response.fileName()+" "+e);
        }
    }

    private Path resolvePath(UUID id, String extension) {
        return root.resolve(id.toString() + extension);
    }
}


package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.helper.FilePathProperties;
import com.sprint.mission.discodeit.helper.FileSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * packageName    : com.sprint.mission.discodeit.repository.file
 * fileName       : FileUserBinaryContentRepository
 * author         : doungukkim
 * date           : 2025. 4. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 24.        doungukkim       최초 생성
 */

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
@RequiredArgsConstructor
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final FilePathProperties filePathUtil;

    @Override
    public BinaryContent findById(UUID attachmentId) {
        Path path = filePathUtil.getBinaryContentFilePath(attachmentId);
        if (!Files.exists(path)) return null;

        return FileSerializer.readFile(path, BinaryContent.class);
    }


    @Override
    public List<BinaryContent> findAllByIds(List<UUID> attachmentIds) {
        List<BinaryContent> selectedAttachments = new ArrayList<>();

        for (UUID attachmentId : attachmentIds) {
            Path path = filePathUtil.getBinaryContentFilePath(attachmentId);

            if (!Files.exists(path)) throw new RuntimeException("no binary content matches with attachmentIs");

            selectedAttachments.add(FileSerializer.readFile(path, BinaryContent.class));
        }
        if (selectedAttachments.size() == attachmentIds.size()) {
            return selectedAttachments;
        }
        return Collections.emptyList();
    }


    @Override
    public BinaryContent createBinaryContent(String fileName, Long size, String contentType, byte[] bytes, String extension) {
        BinaryContent binaryContent = new BinaryContent(fileName, size, contentType, bytes, extension);
        Path path = filePathUtil.getBinaryContentFilePath(binaryContent.getId());
        FileSerializer.writeFile(path, binaryContent);
        return binaryContent;
    }

//    @Override
//    public BinaryContent createBinaryContent(UUID id) {
//        BinaryContent binaryContent = new BinaryContent(id);
//        Path path = filePathUtil.getBinaryContentFilePath(binaryContent.getId());
//        FileSerializer.writeFile(path, binaryContent);
//        return binaryContent;
//    }

    @Override
    public void deleteBinaryContentById(UUID attachmentId) {
        Path path = filePathUtil.getBinaryContentFilePath(attachmentId);
        // 잘못된 id 받음
        if (!Files.exists(path)) throw new RuntimeException("삭제하려는 프로필 없음");

        // profile 없음
        if (attachmentId == null) return;

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("삭제중 오류 발생: FileBinaryContentRepository.deleteBinaryContentById", e);
        }
    }
}

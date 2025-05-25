package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent save(BinaryContentCreateRequest request) {
        BinaryContent newBinaryContent = new BinaryContent(
                request.fileName(),
                (long) request.bytes().length,
                request.contentType(),
                request.bytes());

        return binaryContentRepository.save(newBinaryContent);
    }

    @Override
    public List<BinaryContent> saveAll(List<BinaryContentCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
        }
        List<BinaryContent> binaryContents = requests.stream()
                .map(request -> new BinaryContent(
                        request.fileName(),
                        (long) request.bytes().length,
                        request.contentType(),
                        request.bytes()))
                .collect(Collectors.toList());
        return binaryContentRepository.saveAll(binaryContents);
    }

    @Override
    public BinaryContent findById(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found with id: " + id));
    }

    @Override
    public List<BinaryContent> findAllByIds(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids);
    }

    @Override
    public void deleteById(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("BinaryContent not found with id: " + id + ". Cannot delete.");
        }
        binaryContentRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        binaryContentRepository.deleteAllByIdIn(ids);
    }
}

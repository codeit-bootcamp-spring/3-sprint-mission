package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface BinaryContentService {

    // create
    BinaryContent createBinaryContent(BinaryContentCreateRequest request);

    // find
    BinaryContent findBinaryContentById(UUID id);

    // findAll
    List<BinaryContent> findAllBinaryContent(List<UUID> ids);

    // delete
    boolean deleteBinaryContent(UUID id);
}
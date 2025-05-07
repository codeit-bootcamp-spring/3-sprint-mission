package com.sprint.mission.discodeit.entity.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CreateUserRequest(
        UUID id,
        String username,
        String email,
        String password) {}

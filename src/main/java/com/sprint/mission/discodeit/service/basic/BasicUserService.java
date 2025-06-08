package com.sprint.mission.discodeit.service.basic;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import com.sprint.mission.discodeit.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.StorageService;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.dto.UserDto;
import com.sprint.mission.discodeit.service.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.request.UserUpdateRequest;

@Service
public class BasicUserService<StorageService> implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;

    public BasicUserService(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            StorageService binaryContentStorage,
                            BinaryContentRepository binaryContentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.binaryContentStorage = binaryContentStorage;
        this.binaryContentRepository = binaryContentRepository;
    }

    @Transactional
    @Override
    public UserDto create(UserCreateRequest request, MultipartFile profile) throws IOException {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("email", request.email());
        }

        String encPwd = passwordEncoder.encode(request.password());
        BinaryContent bc = null;
        if (profile != null && !profile.isEmpty()) {
            bc = BinaryContent.builder()
                .id(UUID.randomUUID())
                .fileName(profile.getOriginalFilename())
                .size(profile.getSize())
                .contentType(profile.getContentType())
                .bytes(profile.getBytes())
                .build();
            binaryContentStorage.put(bc.getId(), profile.getBytes());
            binaryContentRepository.save(bc);
        }

        User u = User.builder()
            .username(request.username())
            .email(request.email())
            .password(encPwd)
            .profile(bc)
            .build();

        userRepository.save(u);
        return UserDto.from(u);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
            .map(UserDto::from)
            .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest request, MultipartFile profile) throws IOException {
        User u = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User", userId.toString()));

        if (request.newEmail() != null && !request.newEmail().equals(u.getEmail())
            && userRepository.existsByEmail(request.newEmail())) {
            throw new BadRequestException("email", request.newEmail());
        }

        BinaryContent newBc = u.getProfile();
        if (profile != null && !profile.isEmpty()) {
            newBc = BinaryContent.builder()
                .id(UUID.randomUUID())
                .fileName(profile.getOriginalFilename())
                .size(profile.getSize())
                .contentType(profile.getContentType())
                .bytes(profile.getBytes())
                .build();
            binaryContentStorage.put(newBc.getId(), profile.getBytes());
            binaryContentRepository.save(newBc);
        }

        u.update(
            request.newUsername(),
            request.newEmail(),
            request.newPassword() != null ? passwordEncoder.encode(request.newPassword()) : null,
            newBc
        );
        userRepository.save(u);
        return UserDto.from(u);
    }
}
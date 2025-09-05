package com.sprint.mission.discodeit.assembler;

import com.sprint.mission.discodeit.dto.data.BinaryContentData;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.mapper.MultipartFileMapper;
import com.sprint.mission.discodeit.service.command.CreateUserCommand;
import com.sprint.mission.discodeit.service.command.UpdateUserCommand;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class UserCommandAssembler {

  private final MultipartFileMapper multipartFileMapper;

  public CreateUserCommand toCreateCommand(UserCreateRequest request, MultipartFile profile) {
    BinaryContentData profileData = multipartFileMapper.toBinaryContentData(profile);
    return new CreateUserCommand(
        request.email(),
        request.username(),
        request.password(),
        profileData);
  }

  public UpdateUserCommand toUpdateCommand(UUID userId, UserUpdateRequest request,
      MultipartFile profile) {
    BinaryContentData profileData = multipartFileMapper.toBinaryContentData(profile);
    return new UpdateUserCommand(
        userId,
        request.newUsername(),
        request.newEmail(),
        request.newPassword(),
        profileData);
  }
}


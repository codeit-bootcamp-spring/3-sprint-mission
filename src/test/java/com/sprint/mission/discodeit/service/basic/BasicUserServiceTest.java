package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.testutil.TestConstants.DUPLICATE_EMAIL;
import static com.sprint.mission.discodeit.testutil.TestConstants.NEW_EMAIL;
import static com.sprint.mission.discodeit.testutil.TestConstants.NEW_USERNAME;
import static com.sprint.mission.discodeit.testutil.TestConstants.NON_EXISTENT_USER_ID;
import static com.sprint.mission.discodeit.testutil.TestConstants.TEST_EMAIL;
import static com.sprint.mission.discodeit.testutil.TestConstants.TEST_USERNAME;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.USER_ID_1;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createBinaryContentCreateRequest;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createDefaultUser;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createDefaultUserCreateRequest;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createDefaultUserDto;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createUserDto;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createUserDtoList;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createUserList;
import static com.sprint.mission.discodeit.testutil.TestDataBuilder.createUserUpdateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.MapperFacade;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicUserService 단위 테스트")
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private BinaryContentService binaryContentService;

  @Mock
  private MapperFacade mapperFacade;

  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("사용자 생성 성공 - 프로필 이미지 없음")
  void createUser_Success_WithoutProfile() {
    // Given
    UserCreateRequest request = createDefaultUserCreateRequest();
    User savedUser = createDefaultUser();
    UserDto expectedDto = createDefaultUserDto();

    given(userRepository.existsByEmail(TEST_EMAIL)).willReturn(false);
    given(userRepository.existsByUsername(TEST_USERNAME)).willReturn(false);
    given(binaryContentService.createFromOptional(Optional.empty())).willReturn(null);
    given(userRepository.save(any(User.class))).willReturn(savedUser);
    given(mapperFacade.toDto(savedUser)).willReturn(expectedDto);

    // When
    UserDto result = userService.create(request, Optional.empty());

    // Then
    assertThat(result).isEqualTo(expectedDto);
    then(userRepository).should().existsByEmail(TEST_EMAIL);
    then(userRepository).should().existsByUsername(TEST_USERNAME);
    then(userRepository).should().save(any(User.class));
    then(userStatusRepository).should().save(any(UserStatus.class));
    then(mapperFacade).should().toDto(savedUser);
  }

  @Test
  @DisplayName("사용자 생성 성공 - 프로필 이미지 포함")
  void createUser_Success_WithProfile() {
    // Given
    UserCreateRequest request = createDefaultUserCreateRequest();
    User savedUser = createDefaultUser();
    UserDto expectedDto = createDefaultUserDto();
    Optional<com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest> profileRequest = Optional
        .of(createBinaryContentCreateRequest());

    given(userRepository.existsByEmail(TEST_EMAIL)).willReturn(false);
    given(userRepository.existsByUsername(TEST_USERNAME)).willReturn(false);
    given(binaryContentService.createFromOptional(profileRequest)).willReturn(null);
    given(userRepository.save(any(User.class))).willReturn(savedUser);
    given(mapperFacade.toDto(savedUser)).willReturn(expectedDto);

    // When
    UserDto result = userService.create(request, profileRequest);

    // Then
    assertThat(result).isEqualTo(expectedDto);
    then(userRepository).should().existsByEmail(TEST_EMAIL);
    then(userRepository).should().existsByUsername(TEST_USERNAME);
    then(binaryContentService).should().createFromOptional(profileRequest);
    then(userRepository).should().save(any(User.class));
    then(userStatusRepository).should().save(any(UserStatus.class));
  }

  @Test
  @DisplayName("사용자 생성 실패 - 이메일 중복")
  void createUser_Fail_DuplicateEmail() {
    // Given
    UserCreateRequest request = createDefaultUserCreateRequest();
    given(userRepository.existsByEmail(TEST_EMAIL)).willReturn(true);

    // When & Then
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(DuplicateUserException.class);

    then(userRepository).should().existsByEmail(TEST_EMAIL);
    then(userRepository).should(never()).existsByUsername(any());
    then(userRepository).should(never()).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 생성 실패 - 사용자명 중복")
  void createUser_Fail_DuplicateUsername() {
    // Given
    UserCreateRequest request = createDefaultUserCreateRequest();
    given(userRepository.existsByEmail(TEST_EMAIL)).willReturn(false);
    given(userRepository.existsByUsername(TEST_USERNAME)).willReturn(true);

    // When & Then
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(DuplicateUserException.class);

    then(userRepository).should().existsByEmail(TEST_EMAIL);
    then(userRepository).should().existsByUsername(TEST_USERNAME);
    then(userRepository).should(never()).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 정보 수정 성공 - 프로필 이미지 없음")
  void updateUser_Success_WithoutProfile() {
    // Given
    User existingUser = createDefaultUser();
    UserUpdateRequest request = createUserUpdateRequest(NEW_USERNAME, NEW_EMAIL);
    UserDto expectedDto = createUserDto(USER_ID_1, NEW_USERNAME, NEW_EMAIL);

    given(userRepository.findById(USER_ID_1)).willReturn(Optional.of(existingUser));
    given(userRepository.existsByEmail(NEW_EMAIL)).willReturn(false);
    given(userRepository.existsByUsername(NEW_USERNAME)).willReturn(false);
    given(mapperFacade.toDto(existingUser)).willReturn(expectedDto);

    // When
    UserDto result = userService.update(USER_ID_1, request, Optional.empty());

    // Then
    assertThat(result).isEqualTo(expectedDto);
    then(userRepository).should().findById(USER_ID_1);
    then(userRepository).should().existsByEmail(NEW_EMAIL);
    then(userRepository).should().existsByUsername(NEW_USERNAME);
    then(mapperFacade).should().toDto(existingUser);
  }

  @Test
  @DisplayName("사용자 정보 수정 성공 - 프로필 이미지 포함")
  void updateUser_Success_WithProfile() {
    // Given
    User existingUser = createDefaultUser();
    UserUpdateRequest request = createUserUpdateRequest(NEW_USERNAME, NEW_EMAIL);
    UserDto expectedDto = createUserDto(USER_ID_1, NEW_USERNAME, NEW_EMAIL);
    Optional<com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest> profileRequest = Optional
        .of(createBinaryContentCreateRequest());

    // BinaryContent 객체 생성 (null이 아닌 실제 객체)
    BinaryContent newProfile = new BinaryContent("new-profile.jpg", 2048L, "image/jpeg");

    given(userRepository.findById(USER_ID_1)).willReturn(Optional.of(existingUser));
    given(userRepository.existsByEmail(NEW_EMAIL)).willReturn(false);
    given(userRepository.existsByUsername(NEW_USERNAME)).willReturn(false);
    given(binaryContentService.create(any())).willReturn(newProfile);
    given(mapperFacade.toDto(existingUser)).willReturn(expectedDto);

    // When
    UserDto result = userService.update(USER_ID_1, request, profileRequest);

    // Then
    assertThat(result).isEqualTo(expectedDto);
    then(userRepository).should().findById(USER_ID_1);
    then(userRepository).should().existsByEmail(NEW_EMAIL);
    then(userRepository).should().existsByUsername(NEW_USERNAME);
    then(binaryContentService).should().create(any());
    then(mapperFacade).should().toDto(existingUser);
  }

  @Test
  @DisplayName("사용자 정보 수정 실패 - 존재하지 않는 사용자")
  void updateUser_Fail_UserNotFound() {
    // Given
    UserUpdateRequest request = createUserUpdateRequest(NEW_USERNAME, NEW_EMAIL);
    given(userRepository.findById(NON_EXISTENT_USER_ID)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.update(NON_EXISTENT_USER_ID, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);

    then(userRepository).should().findById(NON_EXISTENT_USER_ID);
    then(userRepository).should(never()).existsByEmail(any());
    then(userRepository).should(never()).existsByUsername(any());
  }

  @Test
  @DisplayName("사용자 정보 수정 실패 - 새 이메일 중복")
  void updateUser_Fail_DuplicateEmail() {
    // Given
    User existingUser = createDefaultUser();
    UserUpdateRequest request = createUserUpdateRequest(NEW_USERNAME, DUPLICATE_EMAIL);

    given(userRepository.findById(USER_ID_1)).willReturn(Optional.of(existingUser));
    given(userRepository.existsByEmail(DUPLICATE_EMAIL)).willReturn(true);

    // When & Then
    assertThatThrownBy(() -> userService.update(USER_ID_1, request, Optional.empty()))
        .isInstanceOf(DuplicateUserException.class);

    then(userRepository).should().findById(USER_ID_1);
    then(userRepository).should().existsByEmail(DUPLICATE_EMAIL);
  }

  @Test
  @DisplayName("사용자 삭제 성공")
  void deleteUser_Success() {
    // Given
    User existingUser = createDefaultUser();
    given(userRepository.findById(USER_ID_1)).willReturn(Optional.of(existingUser));

    // When
    userService.delete(USER_ID_1);

    // Then
    then(userRepository).should().findById(USER_ID_1);
    then(userRepository).should().delete(existingUser);
  }

  @Test
  @DisplayName("사용자 삭제 실패 - 존재하지 않는 사용자")
  void deleteUser_Fail_UserNotFound() {
    // Given
    given(userRepository.findById(NON_EXISTENT_USER_ID)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.delete(NON_EXISTENT_USER_ID))
        .isInstanceOf(UserNotFoundException.class);

    then(userRepository).should().findById(NON_EXISTENT_USER_ID);
    then(userRepository).should(never()).delete(any(User.class));
  }

  @Test
  @DisplayName("사용자 조회 성공")
  void findUser_Success() {
    // Given
    User existingUser = createDefaultUser();
    UserDto expectedDto = createDefaultUserDto();

    given(userRepository.findById(USER_ID_1)).willReturn(Optional.of(existingUser));
    given(mapperFacade.toDto(existingUser)).willReturn(expectedDto);

    // When
    UserDto result = userService.find(USER_ID_1);

    // Then
    assertThat(result).isEqualTo(expectedDto);
    then(userRepository).should().findById(USER_ID_1);
    then(mapperFacade).should().toDto(existingUser);
  }

  @Test
  @DisplayName("전체 사용자 목록 조회 성공")
  void findAllUsers_Success() {
    // Given
    List<User> users = createUserList();
    List<UserDto> expectedDtos = createUserDtoList();

    given(userRepository.findAll()).willReturn(users);
    given(mapperFacade.toUserDtoList(users)).willReturn(expectedDtos);

    // When
    List<UserDto> result = userService.findAll();

    // Then
    assertThat(result).isEqualTo(expectedDtos);
    then(userRepository).should().findAll();
    then(mapperFacade).should().toUserDtoList(users);
  }
}
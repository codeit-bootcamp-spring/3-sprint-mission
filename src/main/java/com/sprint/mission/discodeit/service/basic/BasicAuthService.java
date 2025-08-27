package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.retries.api.TokenAcquisitionFailedException;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;
  private final SessionManager sessionManager;

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  @Override
  public UserDto updateRole(RoleUpdateRequest request) {
    return updateRoleInternal(request);
  }

  @Transactional
  @Override
  public UserDto updateRoleInternal(RoleUpdateRequest request) {
    UUID userId = request.userId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Role newRole = request.newRole();
    user.updateRole(newRole);

    sessionManager.invalidateSessionsByUserId(userId);

    // 로그인 상태라면 강제 로그아웃 처리
    if (jwtRegistry.hasActiveJwtInformationByUserId(userId.toString())) {
      jwtRegistry.invalidateJwtInformationByUserId(userId.toString());
    }

    return userMapper.toDto(user);
  }

  @Override
  public JwtDto refreshToken(String refreshToken, HttpServletResponse response) {

    if(refreshToken == null || !jwtTokenProvider.verifyRefreshToken(refreshToken)){
      throw new UserNotFoundException();
    }

    String userId = jwtTokenProvider.extractUserId(refreshToken);
    if(!jwtRegistry.hasActiveJwtInformationByUserId(userId)){
      throw new TokenAcquisitionFailedException("올바르지 않은 리프레쉬 토큰입니다.");
    }

    String username = jwtTokenProvider.extractUsername(refreshToken);

    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);

    try {
      String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails, response);
      String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails,response);
      UserDto userDto = userDetails.getUserDto();

      JwtInformation newJwtInformation = new JwtInformation(userDto, newAccessToken, newRefreshToken);
      jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

      return new JwtDto(userDto, newAccessToken);

    } catch (Exception e) {
      throw new IllegalArgumentException();
    }

  }
}

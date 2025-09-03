package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.exception.user.UserAccessDeniedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.TokenRefreshService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final TokenRefreshService tokenRefreshService;
    private final JwtRegistry jwtRegistry;

  /**
   * CSRF 토큰 발급 API
   * <p>
   * 프론트엔드에서 CSRF 토큰을 받아올 수 있도록 제공하는 엔드포인트.
   * Spring Security가 자동으로 CsrfToken 객체를 주입해준다.
   *
   * @param csrfToken Spring Security가 자동 주입하는 CSRF 토큰
   * @return CSRF 토큰 정보
   * </p>*/
  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청 : {}", tokenValue);

    // GET 요청에는 CSRF 인증이 이루어지지 않기 때문에 토큰이 초기화 되지 않는다.
    // 따라서 명시적으로 메소드에서 토큰을 호출한다.
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT) // 상태 코드 203
        .build();
  }

  /**
   * Refresh 토큰을 활용한 Access 토큰 재발급 API
   * 엔드포인트 : POST /api/auth/refresh
   * 요청 : Cookie의 REFRESH-TOKEN 사용
   * 응답 : 200 JwtDto ( 새로운 Access 토큰 + Refresh 토큰 )
   * */
  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = extractRefreshTokenFromCookie(request);

    JwtDto jwtDto = tokenRefreshService.refreshTokens(refreshToken, request, response);

    return ResponseEntity.ok(jwtDto);
  }

  /**
   * 쿠키에서 Refresh 토큰 추출
   * */
  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("REFRESH_TOKEN".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  /**
   * 사용자 권한 업데이트 API - JwtRegistry를 활용한 강제 로그아웃 추가
   */
  @PutMapping("/role")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<UserDto> updateUserRole(
      @RequestBody @Valid UserRoleUpdateRequest request,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    log.debug("사용자 권한 업데이트 요청 : userId = {}, newRole = {}, requestBy = {} " ,
        request.userId(), request.newRole(), userDetails.getUsername());

    try {
      // 권한이 변경된 사용자가 로그인 상태라면 강제 로그아웃 ( JwtRegistry 활용 )
      if (jwtRegistry.hasActiveJwtInformationByUserId(request.userId())) {
        jwtRegistry.invalidateJwtInformationByUserId(request.userId());
        log.info("권한 변경으로 인한 강제 로그아웃 완료: userId={}", request.userId());
      }

      UserDto updatedUser = authService.updateUserRole(request.userId(), request.newRole());

      log.debug("사용자 권한 업데이트 완료 : userID = {}, newRole = {} ",
          updatedUser.id(), updatedUser.role());

      return ResponseEntity.ok(updatedUser);

    } catch (AccessDeniedException e) {
      log.error("권한 없음: userId={}", request.userId(), e);
      throw new UserAccessDeniedException();
    } catch (UserNotFoundException e) {
      log.error("사용자를 찾을 수 없음: userId={}", request.userId(), e);
      throw e;
    } catch (Exception e) {
      log.error("사용자 권한 업데이트 중 오류 발생: userId={}", request.userId(), e);
      throw new RuntimeException("사용자 권한 업데이트 중 오류가 발생했습니다.", e);
    }
  }
}
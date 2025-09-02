package com.sprint.mission.discodeit.dto.jwt;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;

/**
 * 클라이언트에 전달되는 JWT 응답 객체 로그인이나 토큰 재발급 요청이 성공 했을 때 유저 정보와 Access 토큰을 담아 반환
 *
 * @param userDto     사용자 식별 및 표시용 정보(id, username, email 등)
 * @param accessToken API 요청 시 Authorization 헤더로 전달할 JWT Access Token
 */
public record JwtDto(
    UserResponseDto userDto,
    String accessToken
) {

}

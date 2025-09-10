package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final org.springframework.security.core.userdetails.UserDetailsService discodeitUserDetailsService;
  private final GrantedAuthoritiesMapper authoritiesMapper;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        String token = authorizationHeader.substring(7);
        authenticateWithToken(token, accessor);
      }
    }
    return message;
  }

  private void authenticateWithToken(String token, StompHeaderAccessor accessor) {
    String username = jwtTokenProvider.getUsernameFromToken(token);
    UserDetails userDetails = discodeitUserDetailsService.loadUserByUsername(username);
    Collection<? extends GrantedAuthority> mapped = authoritiesMapper.mapAuthorities(
        userDetails.getAuthorities());

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            mapped
        );

    accessor.setUser(authentication);
  }
}

package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);


        // 토큰 존재확인
        if(StringUtils.hasText(token)){
            // 토큰 검증
            if(jwtTokenProvider.verifyAccessToken(token)){
                String username = jwtTokenProvider.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                /*
                WebAuthenticationDetails 객체를 만들어서 Authentication 객체에 넣습니다.
                여기에는 요청의 부가 정보가 들어갑니다.
                remoteAddress (클라이언트 IP)
                sessionId (세션 ID)
                나중에 SecurityContextHolder.getContext().getAuthentication().getDetails() 로 꺼내서 이 사용자가 어떤 IP에서 로그인했는지" 등을 추적할 수 있습니다.
                 */
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            String responseBody = objectMapper.createObjectNode()
                    .put("success",false)
                    .put("message","JWT authentication failed")
                    .toString();

            response.getWriter().write(responseBody);

            return;
        }
        }catch (Exception e){
            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            String responseBody = objectMapper.createObjectNode()
                    .put("success",false)
                    .put("message","JWT authentication failed")
                    .toString();

            response.getWriter().write(responseBody);
            return;
        }
        filterChain.doFilter(request, response);

    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

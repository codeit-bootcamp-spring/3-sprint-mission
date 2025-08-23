package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

/**
 * SPA(Single Page Application) 환경을 위한 CSRF 토큰 처리 핸들러
 *
 * <ul>
 *   <li>요청 헤더에 CSRF 토큰이 존재할 경우 → {@link CsrfTokenRequestAttributeHandler} 사용</li>
 *   <li>요청 헤더에 CSRF 토큰이 없을 경우 → {@link XorCsrfTokenRequestAttributeHandler} 사용</li>
 * </ul>
 *
 * <p>클라이언트(SPA)가 헤더로 CSRF 토큰을 전송해도 정상적으로 처리될 수 있도록 지원함</p>
 */
public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    // 일반적인 CSRF 토큰 처리기( 헤더에서 직접 토큰 추출 )
    private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();

    // Spring Security 기본 XOR 기반 토큰 처리기
    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

    /**
     * 요청에서 CSRF 토큰 값을 추출한다.
     *
     * <p>우선 요청 헤더에 CSRF 토큰 값이 존재하는지 확인하고, 존재한다면 {@code plain} 처리기 사용
     * 존재하지 않을 경우에는 {@code xor} 처리기 사용</p>
     *
     * @param request   현재 HttpServletRequest
     * @param csrfToken 현재 요청에 매핑된 CsrfToken 객체
     * @return 요청에서 추출한 CSRF 토큰 값
     */
    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        // 요청 헤더에서 CSRF 토큰 값 확인
        String headerValue = request.getHeader(csrfToken.getHeaderName());

        // 헤더 값이 있으면 plain, 없으면 xor 기반 처리기 사용
        return (StringUtils.hasText(headerValue) ? this.plain : this.xor)
            .resolveCsrfTokenValue(request, csrfToken);
    }

    /**
     * 요청과 응답에서 CSRF 토큰을 처리
     *
     * <p>{@code xor} 처리기를 우선 호출하여 CSRF 토큰을 request attribute로 등록
     * 이후 {@code csrfToken.get()}을 호출해 실제 토큰을 강제로 생성 / 보장</p>
     *
     * @param request   현재 HttpServletRequest
     * @param response  현재 HttpServletResponse
     * @param csrfToken 지연 로딩되는 CsrfToken Supplier
     */
    @Override
    public void handle(HttpServletRequest request,
        HttpServletResponse response,
        Supplier<CsrfToken> csrfToken) {
        // XOR 기반 핸들러 실행 -> request에 CSRF 토큰 속성 저장
        this.xor.handle(request, response, csrfToken);

        // LazyLoading 방지를 위해 실제 토큰을 즉시 가져와 강제 초기화
        csrfToken.get();
    }
}

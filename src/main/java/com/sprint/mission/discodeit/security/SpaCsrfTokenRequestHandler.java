package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       Supplier<CsrfToken> csrfToken) {
        /*
         * 응답 본문에 렌더링되는 경우 BREACH 공격으로부터 CSRF 토큰을 보호하기 위해 항상 XorCsrfTokenRequestAttributeHandler를 사용합니다.
         */
        this.xor.handle(request, response, csrfToken);
        /*
         * 지연된 토큰이 로드되도록 하여 토큰 값을 쿠키에 렌더링합니다.
         */
        csrfToken.get();
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        String headerValue = request.getHeader(csrfToken.getHeaderName());
        /*
         * 요청에 요청 헤더가 포함되어 있다면 CsrfTokenRequestAttributeHandler를 사용하여 CsrfToken을 해결합니다.
         * 이는 SPA가 원본 CsrfToken이 담긴 쿠키를 통해 얻은 헤더 값을 자동으로 포함시키는 경우에 적용됩니다.
         *
         * 다른 모든 경우(예: 요청에 요청 파라미터가 포함된 경우)에는 XorCsrfTokenRequestAttributeHandler를 사용하여 CsrfToken을 해결합니다.
         * 이는 서버 측에서 렌더링된 폼이 숨겨진 입력으로 _csrf 요청 파라미터를 포함하는 경우에 적용됩니다.
         */
        return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request,
                csrfToken);
    }
}
package com.sprint.mission.discodeit.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      Supplier<CsrfToken> csrfToken) {
    /*
     * 항상 XorCsrfTokenRequestAttributeHandler를 사용하여
     * 응답 본문에 CsrfToken이 렌더링될 때 BREACH 공격을 방지합니다.
     */
    this.xor.handle(request, response, csrfToken);
    /*
     * 지연된 토큰을 로드하여 토큰 값을 쿠키에 렌더링합니다.
     */
    csrfToken.get();
  }

  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    String headerValue = request.getHeader(csrfToken.getHeaderName());
    /*
     * 요청에 헤더가 포함되어 있으면 CsrfTokenRequestAttributeHandler를 사용하여
     * CsrfToken을 resolve합니다.
     * 이는 SPA(싱글 페이지 애플리케이션)에서 쿠키로 받은 원본 CsrfToken을
     * 자동으로 헤더에 포함시킬 때 적용됩니다.
     *
     * 그 외의 경우(예: 요청에 파라미터가 포함된 경우)에는
     * XorCsrfTokenRequestAttributeHandler를 사용하여 CsrfToken을 resolve합니다.
     * 이는 서버 사이드 렌더링된 폼에서 _csrf 요청 파라미터를 hidden input으로 포함할 때 적용됩니다.
     */
    return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request,
        csrfToken);
  }
}
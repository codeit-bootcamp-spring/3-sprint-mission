package com.sprint.mission.discodeit.security.translator;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.InvalidCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class SecurityExceptionTranslator {

  public DiscodeitException translate(Exception exception) {
    if (exception instanceof BadCredentialsException) {
      return new InvalidCredentialsException();
    }
    if (exception instanceof AccessDeniedException) {
      return new DiscodeitException(ErrorCode.FORBIDDEN);
    }
    if (exception instanceof AuthenticationException) {
      return new DiscodeitException(ErrorCode.UNAUTHORIZED);
    }
    return new DiscodeitException(ErrorCode.UNAUTHORIZED);
  }
}

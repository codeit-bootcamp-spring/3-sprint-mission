package com.sprint.mission.discodeit.handler;

import jakarta.servlet.ServletException;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import java.io.IOException;

/**
 * PackageName  : com.sprint.mission.discodeit.handler
 * FileName     : CustomSessionExpiredStrategy
 * Author       : dounguk
 * Date         : 2025. 8. 6.
 */
public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {

    }
}

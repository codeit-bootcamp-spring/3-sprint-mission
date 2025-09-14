package com.sprint.mission.discodeit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.controller
 * FileName     : MeWsController
 * Author       : dounguk
 * Date         : 2025. 9. 9.
 */

@Slf4j
@Controller
public class MeWsController {

    @MessageMapping("/me")
    @SendToUser("/queue/me")
    public Map<String, Object> me(Principal principal) {
        String name = (principal != null) ? principal.getName() : "anonymous";
        log.info("[WS] /pub/me 호출 principal={}", name);
        return Map.of(
            "principal", name,
            "ok", true
        );
    }
}

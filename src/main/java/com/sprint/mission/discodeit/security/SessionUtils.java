//package com.sprint.mission.discodeit.security;
//
//
//import org.springframework.security.core.session.SessionRegistry;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SessionUtils {
//
//
//    public SessionUtils(@org.springframework.beans.factory.annotation.Autowired(required = false)
//                        SessionRegistry sessionRegistry) {
//        this.sessionRegistry = sessionRegistry;
//    }
//
//    public boolean isUserLoggedIn(java.util.UUID userId) {
//        if (sessionRegistry == null) {
//            return false; // 무상태 모드에선 항상 false
//        }
//        return sessionRegistry.getAllPrincipals().stream()
//                .filter(p -> p instanceof DiscodeitUserDetails)
//                .map(DiscodeitUserDetails.class::cast)
//                .filter(d -> d.getUserDto().id().equals(userId))
//                .flatMap(d -> sessionRegistry.getAllSessions(d, false).stream())
//                .anyMatch(s -> !s.isExpired());
//    }
//}
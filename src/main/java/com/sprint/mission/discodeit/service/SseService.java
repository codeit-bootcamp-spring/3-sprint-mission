package com.sprint.mission.discodeit.service;

import java.util.Collection;
import java.util.UUID;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE(Server-Sent Events) 서비스 인터페이스
 * 분산 환경을 고려한 추상화 계층
 */
public interface SseService {

    /**
     * SSE 연결 생성
     * @param receiverId 수신자 ID
     * @param lastEventId 마지막 이벤트 ID (재연결 시 사용)
     * @return SseEmitter
     */
    SseEmitter connect(UUID receiverId, UUID lastEventId);

    /**
     * 특정 사용자들에게 메시지 전송
     * @param receiverIds 수신자 ID 목록
     * @param eventName 이벤트 이름
     * @param data 전송할 데이터
     */
    void send(Collection<UUID> receiverIds, String eventName, Object data);

    /**
     * 모든 연결된 사용자에게 브로드캐스트
     * @param eventName 이벤트 이름
     * @param data 전송할 데이터
     */
    void broadcast(String eventName, Object data);
}
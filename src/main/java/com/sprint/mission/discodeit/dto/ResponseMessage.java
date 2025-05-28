package com.sprint.mission.discodeit.dto;

public interface ResponseMessage {

    String SUCCESS = "요청이 성공적으로 처리됨";
    String REQUEST_FAIL = "요청 처리 실패";
    String SUCCESS_USER_UPDATE = "User 정보가 성공적으로 수정됨";
    String USER_NOT_FOUND = "사용자를 찾을 수 없음";
    String CHANNEL_NOT_FOUND = "채널을 찾을 수 없음";
    String MESSAGE_NOT_FOUND = "메세지를 찾을 수 없음";
    String BINARY_NOT_FOUND = "첨부파일을 찾을 수 없음";
    String READ_STATUS_NOT_FOUND = "읽음 상태를 찾을 수 없음";
    String USER_OR_CHANNEL_NOT_FOUND = "유저 또는 채널을 찾을 수 없음";
    String DUPLICATE_USER = "이미 존재하는 사용자임";
    String PASSWORD_VALID = "비밀번호가 일치하지 않음";
    String DUPLICATE_READ_STATUS = "이미 존재하는 읽음 상태임";
    String FILE_PROCESSING_ERROR = "파일 처리 중 오류가 발생했습니다.";
    String INTERNAL_SERVER_ERROR = "서버 내부 오류가 발생했습니다.";

}

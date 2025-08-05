package com.sprint.mission.discodeit.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {

    /* User 관련 에러 */
    USER_NOT_FOUND("유저를 찾을 수 없습니다."),
    DUPLICATE_EMAIL("중복된 이메일입니다."),
    DUPLICATE_USERNAME("중복된 유저 이름입니다."),

    /* Channel 관련 에러 */
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    DUPLICATE_CHANNEL_NAME("중복된 채널 이름입니다."),
    PRIVATE_CHANNEL_UPDATE("Private 채널은 수정할 수 없습니다."),

    /* Message 관련 에러 */
    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),

    /* Auth 관련 에러 */
    INVALID_PASSWORD("잘못된 비밀번호입니다."),

    /* BinaryContent 관련 에러 */
    BINARYCONTENT_NOT_FOUND("파일을 찾을 수 없습니다."),

    /* ReadStatus 관련 에러 */
    READSTATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),
    DUPLICATE_READSTATUS("중복된 읽음 상태입니다."),

    /* UserStatus 관련 에러 */
    USERSTATUS_NOT_FOUND("유저 상태 정보를 찾을 수 없습니다."),
    DUPLICATE_USERSTATUS("중복된 유저 상태 정보입니다.");


    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

}

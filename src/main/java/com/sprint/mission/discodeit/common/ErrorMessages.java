package com.sprint.mission.discodeit.common;

public class ErrorMessages {

    public static final String ERROR_NOT_FOUND = "해당 %s을(를) 찾을 수 없습니다!";
    public static final String ERROR_EXISTS = "이미 등록된 %s입니다!";
    public static final String ERROR_MISMATCH = "%s 불 일치!";

    public static final String ERROR_FILE_UPLOAD_INVALID = "%s 업로드 대상 또는 내용이 잘못되었습니다!";

    public static final String ERROR_PRIVATE_CHANNEL_NOT_UPDATE = "private 채널은 수정할 수 없습니다.";

    public static final String ERROR_EMPTY = "%s 내용은 비어있을 수 없습니다.";

    public static final String ERROR_FILE_SAVE_FAILED = "%s 저장 중 오류가 발생했습니다!";

    public static final String ERROR_FILE_READ_FAILED = "%s 읽기 중 오류가 발생했습니다!";

    public static final String ERROR_FILE_DOWNLOAD_FAILED = "%s 다운로드 중 오류가 발생했습니다!";

    public static final String ERROR_FILE_INIT_FAILED = "%s 초기화 중 오류가 발생했습니다!";

    public static final String ERROR_CURSOR_INVALID = "잘못된 커서 형식입니다. ISO-8601 형식이어야 합니다.";

    public static String format(String entityName, String messageTemplate) {
        return String.format(messageTemplate, entityName);
    }

}

package com.sprint.mission.discodeit.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

// 사용 보류
public class UserResponse {

    @Schema(description = "HTTP 응답 상태코드", example = "200")
    private int httpStatus;
    @Schema(description = "HTTP 응답 메세지", example = "OK")
    private String message;
    @Schema(description = "HTTP 응답 데이터", example = "{ \"id\": 1, \"name\": \"홍길동\" }")
    private Map<String, Object> results;

    public UserResponse(int httpStatus, String message, Map<String, Object> results) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.results = results;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
            "httpStatus=" + httpStatus +
            ", message='" + message + '\'' +
            ", results=" + results +
            '}';
    }
}

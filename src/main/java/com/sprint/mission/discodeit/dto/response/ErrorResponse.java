package com.sprint.mission.discodeit.dto.response;

public class ErrorResponse {
    private int status;
    private String exceptionType;
    private String code;
    private String message;
    private Object details;

    public ErrorResponse(int status, String exceptionType, String code, String message, Object details) {
        this.status = status;
        this.exceptionType = exceptionType;
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public int getStatus() { return status; }
    public String getExceptionType() { return exceptionType; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Object getDetails() { return details; }

    public void setStatus(int status) { this.status = status; }
    public void setExceptionType(String exceptionType) { this.exceptionType = exceptionType; }
    public void setCode(String code) { this.code = code; }
    public void setMessage(String message) { this.message = message; }
    public void setDetails(Object details) { this.details = details; }
}

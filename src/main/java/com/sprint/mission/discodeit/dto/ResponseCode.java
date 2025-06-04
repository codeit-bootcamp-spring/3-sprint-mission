package com.sprint.mission.discodeit.dto;

public interface ResponseCode {

    String SUCCESS = "Success";
    String REQUEST_FAIL = "Request Fail";
    String USER_NOT_FOUND = "User Not Found";
    String CHANNEL_NOT_FOUND = "Channel Not Found";
    String MESSAGE_NOT_FOUND = "Message Not Found";
    String READ_STATUS_NOT_FOUND = "Read Status Not Found";
    String BINARY_NOT_FOUND = "Binary Not Found";
    String PASSWORD_VALID = "Password Not Valid";
    String USER_OR_CHANNEL_NOT_FOUND = "User Or Channel Not Found";
    String DUPLICATE_USER = "Duplicated User";
    String DUPLICATE_READ_STATUS = "Duplicated Read Status";
    String FILE_PROCESSING_ERROR = "File Processing Error";
    String INTERNAL_ERROR = "Internal Server Error";
    String DUPLICATE_USER_STATUS = "Duplicated User Status";
    String USER_STATUS_NOT_FOUND = "User Status Not Found";
}

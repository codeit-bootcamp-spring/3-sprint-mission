package com.sprint.mission.discodeit.dto;


public record CreateBinaryContentRequest(String filename,String contentType, byte[] bytes) {}



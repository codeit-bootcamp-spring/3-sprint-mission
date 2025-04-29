package com.sprint.mission.discodeit.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;

//Instant타입을 Json으로 직렬화/역직렬화 테스트
public class JsonConfig {
    public static void main(String[] args) {
        try{
            // objectMapper 생성
            ObjectMapper objectMapper = new ObjectMapper();

            // JavaTimeModule 등록
            objectMapper.registerModule(new JavaTimeModule());

            // Instant 객체 직렬화
            Instant now = Instant.now();
            String json = objectMapper.writeValueAsString(now);
            System.out.println(json);

            // Instant 객체 역직렬화
            Instant deserializedInstant = objectMapper.readValue(json, Instant.class);
            System.out.println(deserializedInstant);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

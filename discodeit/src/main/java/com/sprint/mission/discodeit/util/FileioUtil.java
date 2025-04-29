package com.sprint.mission.discodeit.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;


// 멘토링때 들은 방식대로 파일과 디렉터리 생성/초기화 로직을 분리
public class FileioUtil {
    // 파일 저장
    public static void init(Path directory) {
        Path parentDirectory = directory.getParent();
        // 저장할 경로의 파일 초기화
        if (!Files.exists(parentDirectory)) {
            try {
                Files.createDirectories(parentDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!Files.exists(directory)) {
            try {
                Files.createFile(directory);
                System.out.println("파일 생성 완료");
            } catch (IOException e) {
                throw new RuntimeException("파일 생성 실패: " + e.getMessage(), e);
            }
        } else {
            try {
                FileWriter writer = new FileWriter(directory.toString());
                writer.write(""); // 내용을 빈 문자열로 덮어씀
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("파일 초기화 실패: " + e.getMessage(), e);
            }
        }
    }

    public static <T> Map<String, T> load(Path path, Class<T> valueType) {
        try {
            ObjectMapper mapper = JsonUtil.getObjectMapper();

            if (!Files.exists(path) || Files.size(path) == 0) {
                return new HashMap<>();
            }

            String json = new String(Files.readAllBytes(path)).trim();
            if (json.isEmpty()) {
                return new HashMap<>();
            }

            return mapper.readValue(json, new TypeReference<Map<String, T>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static <T> void save(Path path, Map<String, T> data) {
        try {
            ObjectMapper mapper = JsonUtil.getObjectMapper();

            String json = mapper.writeValueAsString(data);
            Files.write(path, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.sprint.mission.discodeit.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class EnvLoader {
    public static void loadToSystemProperties(String path) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            props.load(fis);
            for (String name : props.stringPropertyNames()) {
                System.setProperty(name, props.getProperty(name));
            }
        } catch (IOException e) {
            throw new RuntimeException("❌ .env 파일을 읽을 수 없습니다: " + path, e);
        }
    }
}

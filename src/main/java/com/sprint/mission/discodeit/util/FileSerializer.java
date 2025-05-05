package com.sprint.mission.discodeit.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;

/**
 * packageName    : com.sprint.mission.discodeit.util
 * fileName       : FileSerializer
 * author         : doungukkim
 * date           : 2025. 4. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 16.        doungukkim       최초 생성
 */
@Component
public class FileSerializer {

    public static <T> T readFile(Path path, Class<T> theClass) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            Object object = ois.readObject();
            return theClass.cast(object);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void writeFile(Path path, T object){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

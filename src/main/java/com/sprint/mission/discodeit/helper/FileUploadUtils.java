package com.sprint.mission.discodeit.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * packageName    : com.sprint.mission.discodeit.helper
 * fileName       : FileUploadUtils
 * author         : doungukkim
 * date           : 2025. 5. 8.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 8.        doungukkim       최초 생성
 */
@Component
public class FileUploadUtils {


    @Value("${file.upload.all.path}")
    private String path;

    public String getUploadPath(String subDirectory) {
        String basePath;

        basePath = new File(path).getAbsolutePath();

        String uploadPath = basePath + "/" + subDirectory;
        File directory = new File(uploadPath);

        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new RuntimeException(uploadPath);
            }
        }
        return uploadPath;
    }
}

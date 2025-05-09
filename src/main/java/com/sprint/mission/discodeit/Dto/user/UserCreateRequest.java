package com.sprint.mission.discodeit.Dto.user;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * packageName    : com.sprint.mission.discodeit.Dto
 * fileName       : UserServiceDto
 * author         : doungukkim
 * date           : 2025. 4. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 24.        doungukkim       최초 생성
 */


public record UserCreateRequest (
    String username,
    String email,
    String password

){

}


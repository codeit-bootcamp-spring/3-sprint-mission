package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.userStatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * packageName    : com.sprint.mission.discodeit.controller fileName       : UserStatusController
 * author         : doungukkim date           : 2025. 5. 9. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 5. 9.        doungukkim 최초 생성
 */
@Controller
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserStatusController {
    private final UserStatusService userStatusService;
}

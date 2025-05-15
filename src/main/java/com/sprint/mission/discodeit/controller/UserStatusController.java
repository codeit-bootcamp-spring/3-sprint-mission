package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user-status")
public class UserStatusController {

    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UserStatus> create(@RequestBody UserStatusCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userStatusService.create(request));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{userId}")
    @ResponseBody
    public ResponseEntity<UserStatus> find(@PathVariable("userId") UUID id) {
        return ResponseEntity.ok(userStatusService.find(id));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<UserStatus>> findAll() {
        return ResponseEntity.ok(userStatusService.findAll());
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<UserStatus> update(@RequestBody UserStatusUpdateRequest request) {
        return ResponseEntity.ok(userStatusService.update(request));
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{userId}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID id) {
        userStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

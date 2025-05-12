package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequiredArgsConstructor
public class BasicController {

    private final ChannelService channelService;
    private final UserService userService;
    private final MessageService messageService;

    @RequestMapping(value="/register", method = RequestMethod.POST)
    public String register(@RequestBody CreateUserRequest request, @RequestBody CreateBinaryContentRequest binaryContentRequest){
        userService.create(request, binaryContentRequest);

        return "register";
    }

}

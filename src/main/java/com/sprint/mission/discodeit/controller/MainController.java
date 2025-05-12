package com.sprint.mission.discodeit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String main() {
        return "redirect:/user-list.html";

    }

    @GetMapping("/ai")
    public String userList() {
        return "redirect:/user-list-by-ai.html";
    }
}

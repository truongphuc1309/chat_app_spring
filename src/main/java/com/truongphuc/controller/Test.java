package com.truongphuc.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class Test {
    @MessageMapping("/greet")
    @SendTo ("/topic/greet")
    public String Greet (String mess){
        return "Hello " + mess;
    }
}

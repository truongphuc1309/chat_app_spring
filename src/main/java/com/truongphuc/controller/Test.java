package com.truongphuc.controller;

import com.truongphuc.dto.ConversationDto;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.service.CloudinaryService;
import com.truongphuc.util.ConversationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class Test {
    final private CloudinaryService cloudinaryService;
    final private ConversationUtil conversationUtil;

    @GetMapping(value = "/test")
    public List<ConversationDto> test () throws Exception {
//        return conversationUtil.getConversationsOfUser("ff8080819331277301933127ae160001", PageRequest.of(0, 10));

        return null;
    }
}

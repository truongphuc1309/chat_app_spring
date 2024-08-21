package com.truongphuc.util.impl;

import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.UserEntity;
import com.truongphuc.util.ConversationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ConversationUtilImpl implements ConversationUtil {
    @Override
    public boolean isAdminOfConversation(UserEntity userEntity, ConversationEntity conversation) {
        return conversation.getCreatedBy().equals(userEntity);
    }
}

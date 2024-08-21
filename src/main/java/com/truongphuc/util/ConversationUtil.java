package com.truongphuc.util;

import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.UserEntity;

public interface ConversationUtil {
    boolean isAdminOfConversation(UserEntity user, ConversationEntity conversation);
}

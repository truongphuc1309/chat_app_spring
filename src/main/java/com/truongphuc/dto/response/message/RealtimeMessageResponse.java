package com.truongphuc.dto.response.message;

import com.truongphuc.constant.MessageAction;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RealtimeMessageResponse {
    MessageAction action;
    MessageDetailsResponse data;
}

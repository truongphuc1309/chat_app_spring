package com.truongphuc.dto.response.message;

import com.truongphuc.dto.MemberDto;
import com.truongphuc.dto.response.user.UserProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LastReadMessageResponse {
    private String id;
    private MemberDto reader;
}

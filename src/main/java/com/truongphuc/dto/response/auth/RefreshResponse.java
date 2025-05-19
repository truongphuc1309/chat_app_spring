package com.truongphuc.dto.response.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class RefreshResponse implements Serializable {
    String email;
    String accessToken;
    String refreshToken;
}

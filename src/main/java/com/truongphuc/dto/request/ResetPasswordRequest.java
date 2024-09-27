package com.truongphuc.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ResetPasswordRequest {
    @NotNull(message = "newPassword is required")
    @NotBlank(message = "newPassword is required")
    String newPassword;

    @NotNull(message = "confirmPassword is required")
    @NotBlank(message = "confirmPassword is required")
    String confirmPassword;
}

package com.truongphuc.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ChangePasswordRequest {
    @NotNull(message = "newPassword is required")
    @NotBlank(message = "newPassword is required")
    @Size(min = 8, message = "Password must be 8 characters at least")

    String currentPassword;

    @NotNull(message = "confirmPassword is required")
    @NotBlank(message = "confirmPassword is required")
    @Size (min = 8, message = "Password must be 8 characters at least")
    String newPassword;
}

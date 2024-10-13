package com.truongphuc.dto.request.auth;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class SignUpRequest implements Serializable{
    @NotBlank (message = "Email required")
    @Email (message = "Invalid email")
    String email;

    @NotBlank (message = "Name required")
    String name;

    @NotBlank (message = "Password required")
    @Size (min = 8, message = "Password must be 8 characters at least")
    String password;
}

package com.truongphuc.dto.request.auth;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class LogInRequest implements Serializable{
    @NotBlank (message = "Email required")
    @Email (message = "Invalid email")
    String email;

    @NotBlank (message = "Password required")
    @Size (min = 8, message = "Password must be 8 characters at least")
    String password;
}

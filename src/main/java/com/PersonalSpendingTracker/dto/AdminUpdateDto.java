package com.PersonalSpendingTracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class AdminUpdateDto {

        @NotEmpty(message = "Username cannot be empty")
        private String userName;

        @Email(message = "Invalid email format")
        private String email;

        private String password;

        @Size(min = 7, max = 15, message = "Phone number must be between 7 and 15 characters")
        private String phone;

        private Instant createdTimestamp;
}

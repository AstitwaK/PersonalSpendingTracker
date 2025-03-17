package com.PersonalSpendingTracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class AdminUpdateDto {

        @Size(min = 3, max = 30, message = "UserName must be between 3 and 30 characters")
        private String userName;

        @Email(message = "Email should be valid")
        private String email;

        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @Size(min = 7, max = 15, message = "Phone number must be between 7 and 15 characters")
        private String phone;

        private Instant createdTimestamp;
}

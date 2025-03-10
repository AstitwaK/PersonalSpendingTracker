package com.PersonalSpendingTracker.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class AdminUpdateDto {
        private String userName;
        private String email;
        private String password;
        private String phone;
        private Instant createdTimestamp;
}

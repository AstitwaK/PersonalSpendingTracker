package com.PersonalSpendingTracker.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserUpdateDto {
    private String userName;
    private String email;
    private String password;
    private int phone;
}

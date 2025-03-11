package com.PersonalSpendingTracker.dto;

import lombok.*;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ExpenseUpdateDto {
    private String expName;
    private String date;
    private String costOfExp;
    private String quantity;
    private Instant createdTimestamp;
}

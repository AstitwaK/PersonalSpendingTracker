package com.PersonalSpendingTracker.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Setter
@Getter
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

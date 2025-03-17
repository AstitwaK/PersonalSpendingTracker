package com.PersonalSpendingTracker.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseUpdateDto {
    @NotEmpty(message = "Expense name cannot be null")
    private String expName;

    @NotNull(message = "Date cannot be null")
    private String date;

    @Positive(message = "Cost should be greater than zero")
    private Double costOfExp;

    @Positive(message = "Quantity should be greater than zero")
    private Integer quantity;

    private Instant createdTimestamp;
}

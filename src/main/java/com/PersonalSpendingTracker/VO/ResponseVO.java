package com.PersonalSpendingTracker.VO;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVO {
    private String status;
    private String message;
    private Object data;
}


package com.PersonalSpendingTracker.VO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVO {
    private String status;
    private String message;
    private Object data;
}


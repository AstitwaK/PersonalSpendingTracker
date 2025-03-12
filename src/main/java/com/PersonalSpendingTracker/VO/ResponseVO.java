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

    /* TODO
    *   1. remove httpStatus from ResponseVO
    *       this can be directly sent in ResponseEntity.
    *   e.g. new ResponseEntity<>(new ResponseVO("SUCCESS", "your message", data), HttpStatus.OK)
    * */
    private HttpStatus httpStatus;
}


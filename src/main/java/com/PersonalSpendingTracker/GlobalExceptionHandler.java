//package com.PersonalSpendingTracker;
//
//import com.PersonalSpendingTracker.VO.ResponseVO;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import java.util.stream.Collectors;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ResponseVO> handleValidationException(MethodArgumentNotValidException ex) {
//        String errors = ex.getBindingResult().getFieldErrors()
//                .stream().map(err -> err.getField() + " " + err.getDefaultMessage())
//                .collect(Collectors.joining(", "));
//
//        ResponseVO response = new ResponseVO("Validation Error", errors, null);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(UserNotFoundException.class)
//    public ResponseEntity<ResponseVO> handleUserNotFoundException(UserNotFoundException ex) {
//        ResponseVO response = new ResponseVO("Error", ex.getMessage(), null);
//        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//    }
//}

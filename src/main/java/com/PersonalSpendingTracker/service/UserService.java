package com.PersonalSpendingTracker.service;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.dto.UserRequestDto;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Method for user login
    public ResponseEntity<ResponseVO> login(String name, String password) {
        Optional<User> userOptional = userRepository.findActiveUserByUserName(name);

        if (userOptional.isEmpty()) {
            log.error("User name not found: {}", name);
            return buildErrorResponse("User name not found", HttpStatus.BAD_REQUEST);
        }

        User user = userOptional.get();
        if (validatePassword(user, password)) {
            log.info("User successfully logged in: {}", name);
            return buildSuccessResponse("User successfully logged in", user);
        } else {
            log.error("Incorrect password attempt for user: {}", name);
            return buildErrorResponse("User entered wrong password", HttpStatus.UNAUTHORIZED);
        }
    }

    // Method for user registration
    public ResponseEntity<ResponseVO> register(UserRequestDto userRequest) {
        if (userRepository.findActiveUserByUserNameAndEmail(userRequest.getUserName(), userRequest.getEmail()).isPresent()) {
            log.error("User name or email already exists: {}", userRequest.getUserName());
            return buildErrorResponse("User Name or email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = createUser(userRequest.getUserName(), userRequest.getEmail(), userRequest.getPassword(), userRequest.getPhone());
        userRepository.save(user);
        log.info("User successfully registered: {}", userRequest.getUserName());
        return buildSuccessResponse("User successfully registered", user);
    }

    // Method to change the password
    public ResponseEntity<ResponseVO> passwordChange(Long id, String password) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setPassword(encodeBase64(password));
                    userRepository.save(user);
                    log.info("Password successfully updated for user ID: {}", id);
                    return buildSuccessResponse("Password Changed", user);
                })
                .orElseGet(() -> {
                    log.error("User not found for password change: {}", id);
                    return buildErrorResponse("User not found", HttpStatus.BAD_REQUEST);
                });
    }

    // Method to handle forgotten password requests
    public ResponseEntity<ResponseVO> forgotPassword(String phone) {
        return userRepository.findByPhone(phone)
                .map(user -> {
                    log.info("Password reset request received for phone: {}", phone);
                    return buildSuccessResponse("Password reset", user);
                })
                .orElseGet(() -> {
                    log.error("User not found for phone number: {}", phone);
                    return buildErrorResponse("User not found", HttpStatus.BAD_REQUEST);
                });
    }

    // Helper methods for password validation, encoding, and creating users
    private boolean validatePassword(User user, String password) {
        return decodeBase64(user.getPassword()).equals(password);
    }

    private User createUser(String name, String email, String password, String phone) {
        return User.builder()
                .userName(name)
                .email(email)
                .password(encodeBase64(password))
                .phone(phone)
                .status(true)
                .build();
    }

    private String encodeBase64(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    private String decodeBase64(String encodedPassword) {
        return new String(Base64.getDecoder().decode(encodedPassword));
    }

    private ResponseEntity<ResponseVO> buildSuccessResponse(String message, Object data) {
        return new ResponseEntity<>(new ResponseVO("Success", message, data), HttpStatus.OK);
    }

    private ResponseEntity<ResponseVO> buildErrorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(new ResponseVO("Error", message, null), status);
    }
}

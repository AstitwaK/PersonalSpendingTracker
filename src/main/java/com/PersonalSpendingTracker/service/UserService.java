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

    public ResponseEntity<ResponseVO> login(String name, String password) {
        Optional<User> userOptional = userRepository.findActiveUserByUserName(name);

        if (userOptional.isEmpty()) {
            return buildErrorResponse("User name not found", HttpStatus.BAD_REQUEST);
        }

        User user = userOptional.get();
        if (validatePassword(user, password)) {
            log.info("User successfully logged in");
            return buildSuccessResponse("User successfully logged in", user);
        } else {
            log.error("User entered wrong password");
            return buildErrorResponse("User entered wrong password", HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<ResponseVO> register(UserRequestDto userRequest) {
        if (userRepository.findActiveUserByUserNameAndEmail(userRequest.getUserName(), userRequest.getEmail()).isPresent()) {
            log.error("User Name or email already exists");
            return buildErrorResponse("User Name or email already exists", HttpStatus.BAD_REQUEST);
        }
        User user = createUser(userRequest.getUserName(), userRequest.getEmail(), userRequest.getPassword(), userRequest.getPhone());
        userRepository.save(user);
        log.info("User successfully registered");
        return buildSuccessResponse("User successfully registered", user);
    }

    public ResponseEntity<ResponseVO> passwordChange(Long id, String password) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setPassword(encodeBase64(password));
                    userRepository.save(user);
                    log.info("Password successfully updated");
                    return buildSuccessResponse("Password Changed", user);
                })
                .orElseGet(() -> buildErrorResponse("User not found", HttpStatus.BAD_REQUEST));
    }

    public ResponseEntity<ResponseVO> forgotPassword(String phone) {
        return userRepository.findByPhone(phone)
                .map(user -> {
                    log.info("Password reset request received");
                    return buildSuccessResponse("Password reset", user);
                })
                .orElseGet(() -> buildErrorResponse("User not found", HttpStatus.BAD_REQUEST));
    }

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

    private ResponseEntity<ResponseVO> buildSuccessResponse(String message, Object data) {
        return new ResponseEntity<>(new ResponseVO("Success", message, data), HttpStatus.OK);
    }

    private ResponseEntity<ResponseVO> buildErrorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(new ResponseVO("Error", message, null), status);
    }

    private String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    private String decodeBase64(String encodedString) {
        return new String(Base64.getDecoder().decode(encodedString));
    }
}
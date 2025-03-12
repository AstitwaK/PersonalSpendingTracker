package com.PersonalSpendingTracker.service;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<ResponseVO> login(String name, String password) {
        return userRepository.findActiveUserByUserName(name)
                .map(user -> {
                    if (validatePassword(user, password)) {
                        log.info("User successfully Logged In");
                        ResponseVO response = instantiateResponseVO("Success", "User successfully Logged In", user);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        log.error("User entered wrong password");
                        ResponseVO response = instantiateResponseVO("Error", "User entered wrong password", null);
                        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                    }
                })
                .orElseGet(() -> {
                    ResponseVO response = instantiateResponseVO("Error", "User name not found", null);
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                });
    }

    // Now validatePassword() only checks if the password matches and returns a boolean
    private boolean validatePassword(User user, String password) {
        String decodedPassword = decodeBase64(user.getPassword());
        return decodedPassword.equals(password);
    }

    private ResponseVO instantiateResponseVO(String status, String message, Object data) {
        return new ResponseVO(status, message, data);
    }

    private String decodeBase64(String encodedString) {
        return new String(Base64.getDecoder().decode(encodedString));
    }


    public ResponseEntity<ResponseVO> register(String name, String email, String password, String phone) {
        if (userRepository.findActiveUserByUserNameAndEmail(name, email).isPresent()) {
            log.error("User Name or email already exists");
            return new ResponseEntity<>(instantiateResponseVO("Error", "User Name or email already exists", null), HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());

        User user = User.builder()
                .userName(name)
                .email(email)
                .password(encodedPassword)
                .phone(phone)
                .status(true)
                .build();

        userRepository.save(user);
        log.info("User successfully registered");
        return new ResponseEntity<>(instantiateResponseVO("Success", "User successfully registered", user), HttpStatus.OK);
    }

    public ResponseEntity<ResponseVO> passwordChange(Long id, String password) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setPassword(Base64.getEncoder().encodeToString(password.getBytes()));
                    userRepository.save(user);
                    log.info("Password successfully updated");
                    return new ResponseEntity<>(new ResponseVO("Success", "Password Changed", user), HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.error("Password Not updated");
                    return new ResponseEntity<>(new ResponseVO("Error", "User not found", null), HttpStatus.BAD_REQUEST);
                });
    }

    public ResponseEntity<ResponseVO> forgotPassword(String phone) {
        return userRepository.findByPhone(phone)
                .map(user -> {
                    log.info("Password reset");
                    return new ResponseEntity<>(new ResponseVO("Success", "Password reset", user), HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.error("User with phone not found");
                    return new ResponseEntity<>(new ResponseVO("Error", "User not found", null), HttpStatus.BAD_REQUEST);
                });
    }
}

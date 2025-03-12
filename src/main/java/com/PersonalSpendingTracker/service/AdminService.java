package com.PersonalSpendingTracker.service;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.VO.UserVO;
import com.PersonalSpendingTracker.dto.AdminUpdateDto;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.systemDefault()); // Converts Instant to system timezone

    // Method to get all active users
    public ResponseEntity<ResponseVO> findAllUser() {
        List<UserVO> userVOList = userRepository.getAllActiveUsers().stream()
                .map(this::instantiateUserVO)
                .collect(Collectors.toList());

        // Wrapping the response in ResponseEntity
        ResponseVO response = new ResponseVO("Success", "List of all users", userVOList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private UserVO instantiateUserVO(User user) {
        return new UserVO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                formatInstant(user.getCreatedTimestamp()),
                formatInstant(user.getLastUpdatedTimestamp())
        );
    }

    private String formatInstant(Instant timestamp) {
        return (timestamp == null) ? null : DATE_FORMATTER.format(timestamp);
    }

    // Method to deactivate a user
    public ResponseEntity<ResponseVO> deleteUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setStatus(true);  // Deactivating the user
                    userRepository.save(user);
                    log.info("User {} deactivated successfully", id);
                    ResponseVO response = new ResponseVO("Success", "User deactivated", user);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.warn("User with ID {} not found for deactivation", id);
                    ResponseVO response = new ResponseVO("Error", "User not found", null);
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                });
    }

    /*TODO
    *   1. All the validations are being handled automatically using @VALID annotation.
    *   Please remove below conditions which is starting from line 90
    *  ---> Also read about @RestControllerAdvice and how to implement it.
    *       It is used to handle the exception at controller level and return the response directly
    * */

    // Method to update user details
    public ResponseEntity<ResponseVO> userUpdate(@Valid AdminUpdateDto adminUpdateDto, Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            log.warn("User with ID {} not found", id);
            ResponseVO response = new ResponseVO("Error", "User Not Found", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User existingUser = optionalUser.get();

        // Update only non-null fields
        if (adminUpdateDto.getUserName() != null) {
            existingUser.setUserName(adminUpdateDto.getUserName());
        }
        if (adminUpdateDto.getEmail() != null) {
            existingUser.setEmail(adminUpdateDto.getEmail());
        }
        if (adminUpdateDto.getPassword() != null) {
            String encodedPassword = Base64.getEncoder().encodeToString(adminUpdateDto.getPassword().getBytes());
            existingUser.setPassword(encodedPassword);
        }
        if (adminUpdateDto.getPhone() != null) {
            existingUser.setPhone(adminUpdateDto.getPhone());
        }
        if (adminUpdateDto.getCreatedTimestamp() != null) {
            existingUser.setCreatedTimestamp(adminUpdateDto.getCreatedTimestamp());
        }

        userRepository.save(existingUser);
        log.info("User successfully updated: {}", existingUser);

        ResponseVO response = new ResponseVO("Status", "User Updated Successfully", existingUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


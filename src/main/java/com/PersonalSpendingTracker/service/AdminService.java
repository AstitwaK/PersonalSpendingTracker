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
    // Method to update user details
    public ResponseEntity<ResponseVO> userUpdate(@Valid AdminUpdateDto adminUpdateDto, Long id) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    Optional.ofNullable(adminUpdateDto.getUserName()).ifPresent(existingUser::setUserName);
                    Optional.ofNullable(adminUpdateDto.getEmail()).ifPresent(existingUser::setEmail);
                    Optional.ofNullable(adminUpdateDto.getPassword())
                            .ifPresent(password -> existingUser.setPassword(Base64.getEncoder().encodeToString(password.getBytes())));
                    Optional.ofNullable(adminUpdateDto.getPhone()).ifPresent(existingUser::setPhone);
                    Optional.ofNullable(adminUpdateDto.getCreatedTimestamp()).ifPresent(existingUser::setCreatedTimestamp);

                    userRepository.save(existingUser);
                    log.info("User successfully updated: {}", existingUser);

                    return ResponseEntity.ok(new ResponseVO("Status", "User Updated Successfully", existingUser));
                })
                .orElseGet(() -> {
                    log.warn("User with ID {} not found", id);
                    return ResponseEntity.badRequest().body(new ResponseVO("Error", "User Not Found", null));
                });
    }
}


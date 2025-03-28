package com.PersonalSpendingTracker.service;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.VO.UserVO;
import com.PersonalSpendingTracker.exception.UserNotFoundException;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class AdminService
{
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));

        user.setStatus(false);  // Deactivating the user
        userRepository.save(user);
        log.info("User {} deactivated successfully", id);

        ResponseVO response = new ResponseVO("Success", "User deactivated", user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


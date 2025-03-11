package com.PersonalSpendingTracker.service;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.VO.UserVO;
import com.PersonalSpendingTracker.dto.AdminUpdateDto;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.systemDefault()); // Converts Instant to system timezone

    public ResponseVO findAllUser() {
        List<UserVO> userVOList = userRepository.getAllActiveUsers().stream()
                .map(this::instantiateUserVO)
                .collect(Collectors.toList());

        return new ResponseVO("Success", "List of all users", userVOList);
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

    public ResponseVO deleteUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setStatus(true);
                    userRepository.save(user);
                    log.info("User {} deactivated successfully", id);
                    return new ResponseVO("Success", "User deactivated", user);
                })
                .orElseGet(()->{
                    log.warn("User with ID {} not found for deactivation", id);
                    return new ResponseVO("Error", "User not found", null);
                });
    }

    /*TODO
    *   1. Apply all the validations using spring annotation e.g. @Valid
    * */
    public ResponseVO userUpdate(AdminUpdateDto adminUpdateDto, Long id){
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            log.warn("User with ID {} not found", id);
            return new ResponseVO("Error", "User Not Found", null);
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
        return new ResponseVO("Status", "User Updated Successfully", existingUser);
    }
}

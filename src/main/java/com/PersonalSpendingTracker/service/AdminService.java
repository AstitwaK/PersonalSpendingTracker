package com.PersonalSpendingTracker.service;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.dto.AdminUpdateDto;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    public ResponseVO findAllUser() {
        List<User> users =  userRepository.findBystatusTrue();
        return new ResponseVO("Success","List of all users",users);
    }

    public ResponseVO deactivateById(Long id) {
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

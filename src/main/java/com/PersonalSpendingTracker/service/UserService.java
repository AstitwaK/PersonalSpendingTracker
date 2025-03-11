package com.PersonalSpendingTracker.service;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    /*TODO
     *  3. check how to encode and decode using key
     * */
    public ResponseVO login(String name, String password) {
        return userRepository.findActiveUserByUserName(name)
                .map(user -> validatePassword(user, password))
                .orElseGet(() -> instantiateResponseVO("Error", "User name not found", null));
    }

    private ResponseVO validatePassword(User user, String password) {
        String decodedPassword = decodeBase64(user.getPassword());
        if (decodedPassword.equals(password)) {
            log.info("User successfully Logged In");
            return instantiateResponseVO("Success", "User successfully Logged In", user);
        }
        log.error("User entered wrong password");
        return instantiateResponseVO("Error", "User entered wrong password", null);
    }

    private String decodeBase64(String encodedString) {
        return new String(Base64.getDecoder().decode(encodedString));
    }

    private ResponseVO instantiateResponseVO(String status, String message, Object data) {
        return new ResponseVO(status, message, data);
    }

    public ResponseVO register(String name, String email, String password, String phone) {
        if (userRepository.findActiveUserByUserNameAndEmail(name, email).isPresent()) {
            log.error("User Name or email already exists");
            return instantiateResponseVO("Error", "User Name or email already exists", null);
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
        return instantiateResponseVO("Success", "User successfully registered", user);
    }

    public ResponseVO passwordChange(Long id,String password){
        return userRepository.findById(id)
                .map(user -> {
                    user.setPassword(Base64.getEncoder().encodeToString(password.getBytes()));
                    userRepository.save(user);
                    log.info("Password successfully updated");
                    return new ResponseVO("Success", "Password Changed", user);})
                .orElseGet(()->{
                    log.error("Password Not updated");
                    return new ResponseVO("Error", "User not found", null);
                });
    }

    public ResponseVO forgotPassword(String phone) {
        return userRepository.findByPhone(phone)
                .map(user -> {
                    log.info("Password reset");
                    return new ResponseVO("Success", "Password reset", user);
                })
                .orElseGet(() -> {
                    log.error("User with phone not found");
                    return new ResponseVO("Error", "User not found", null);
                });
    }
}

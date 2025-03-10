package com.PersonalSpendingTracker.service;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public ResponseVO login(String name, String password){
        Optional<User> user = userRepository.findByUserNameAndStatusTrue(name);
        if(user.isPresent()){
            User getUser = user.get();
            byte[] decodedBytes = Base64.getDecoder().decode(getUser.getPassword());
            String decodedString = new String(decodedBytes);
            if (decodedString.equals(password)){
                log.info("User successfully Logged In");
                return new ResponseVO("Success","User successfully Logged In",user);
            }
            log.error("User entered wrong password");
            return new ResponseVO("Error","User entered wrong password",null);
        }
        log.error("User name not found or user is removed");
        return new ResponseVO("Error","User name not found or user is removed",null);
    }

    public ResponseVO register(String name, String email, String password, String phone){
        if (userRepository.findByUserNameAndStatusTrue(name).isPresent() || userRepository.findByemail(email).isPresent()){
            log.error("User Name or email already exists");
            return new ResponseVO("Error","User Name or email already exists", null);
        }
            String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
            User user = new User(null, name, email, encodedPassword, phone, true, null, null);
            userRepository.save(user);
            log.info("User successfully registered");
            return new ResponseVO("Success","User successfully registered", user);
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
        return userRepository.findByphone(phone)
                .map(user -> {
                    log.info("Password reset");
                    return new ResponseVO("Success", "Password reset    ", user);
                })
                .orElseGet(() -> {
                    log.error("User with phone not found");
                    return new ResponseVO("Error", "User not found", null);
                });
    }
}

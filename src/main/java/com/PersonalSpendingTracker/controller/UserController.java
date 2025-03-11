package com.PersonalSpendingTracker.controller;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.repository.UserRepository;
import com.PersonalSpendingTracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseBody
    public ResponseVO login(@RequestParam("name") String name, @RequestParam("password") String password) {
        return userService.login(name,password);
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseVO register(@RequestBody User user) {
        return userService.register(user.getUserName(), user.getPassword(), user.getPassword(), user.getPhone());
    }

    @PostMapping("/forgot")
    @ResponseBody
    public ResponseVO forgotPassword(@RequestParam String phone) {
        return userService.forgotPassword(phone);
    }

    @PostMapping("/password")
    @ResponseBody
    public ResponseVO changePassword(@RequestParam("password") String password,@RequestParam("id") Long id) {
        return userService.passwordChange(id, password);
    }
}

package com.PersonalSpendingTracker.controller;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.dto.UserRequestDto;
import com.PersonalSpendingTracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<ResponseVO> login(@RequestParam("name") String name, @RequestParam("password") String password) {
        return userService.login(name,password);
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<ResponseVO> register(@Valid @RequestBody UserRequestDto userRequest) {
        return userService.register(userRequest);
    }

    @PostMapping("/forgot")
    @ResponseBody
    public ResponseEntity<ResponseVO> forgotPassword(@RequestParam String phone) {
        return userService.forgotPassword(phone);
    }

    @PostMapping("/password")
    @ResponseBody
    public ResponseEntity<ResponseVO> changePassword(@RequestParam("password") String password,@RequestParam("id") Long id) {
        return userService.passwordChange(id, password);
    }
}

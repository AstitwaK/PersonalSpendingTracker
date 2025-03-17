package com.PersonalSpendingTracker.controller;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.service.UserService;
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

    /* TODO
    *     Create UserRequest instead of User entity while registering the user. And apply validations using annotations.
    * */
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<ResponseVO> register(@RequestBody User user) {
        return userService.register(user.getUserName(), user.getPassword(), user.getPassword(), user.getPhone());
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

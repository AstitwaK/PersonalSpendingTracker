package com.PersonalSpendingTracker.controller;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.dto.AdminUpdateDto;
import com.PersonalSpendingTracker.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /*TODO
     *  1. Create a user vo class to return the user details
     *  2. Convert created-timestamp and last-updated-timestamp into readable format date. e.g -> 2025-03-10
     * */
    @GetMapping("/users")
    @ResponseBody
    public ResponseVO adminview() {
        return adminService.findAllUser();
    }

    /*TODO
     *  1. password.equals("0000") put this password in .properties file and read it from there
     *  2. create a separate method to instantiate response vo object and use it to remove redundant code
     * */
    @Value("${admin.password}")
    private String adminPassword;

    @PostMapping("/login")
    @ResponseBody
    public ResponseVO adminLogin(@RequestParam String name, @RequestParam String password) {
        name = name.toLowerCase();
        if ("admin".equals(name) && adminPassword.equals(password)) {
            return createResponse("Success", "Admin logged in successfully");
        } else {
            return createResponse("Error", "Invalid credentials");
        }
    }

    private ResponseVO createResponse(String status, String message) {
        return new ResponseVO(status, message, null);
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseVO updateUser(@PathVariable(value = "id") long id, @RequestBody AdminUpdateDto adminUpdateDto) {
        return adminService.userUpdate(adminUpdateDto,id);
    }

    /* TODO
     *   1. deleteThroughId  rename to deleteUser
     *   2. deactivateById rename to deleteUser
     * */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseVO deleteThroughId(@PathVariable(value = "id") long id) {
        return adminService.deactivateById(id);
    }
}
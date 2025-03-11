package com.PersonalSpendingTracker.controller;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.dto.AdminUpdateDto;
import com.PersonalSpendingTracker.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    @ResponseBody
    public ResponseVO adminView() {
        return adminService.findAllUser();
    }

    @Value("${admin.password}")
    private String adminPassword;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<ResponseVO> adminLogin(@RequestParam String name, @RequestParam String password) {
        name = name.toLowerCase();
        if ("admin".equals(name) && adminPassword.equals(password)) {
            return new ResponseEntity<>(createResponse("Success", "Admin logged in successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(createResponse("Error", "Invalid credentials"), HttpStatus.BAD_REQUEST);
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
    public ResponseVO deleteUser(@PathVariable(value = "id") long id) {
        return adminService.deleteUser(id);
    }
}
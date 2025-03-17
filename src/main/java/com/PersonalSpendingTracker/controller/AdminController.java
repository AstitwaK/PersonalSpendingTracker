package com.PersonalSpendingTracker.controller;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.dto.AdminUpdateDto;
import com.PersonalSpendingTracker.service.AdminService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ResponseVO> adminView() {
        return adminService.findAllUser();
    }

    @Value("${admin.password}")
    private String adminPassword;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<ResponseVO> adminLogin(@RequestParam String name, @RequestParam String password)
    {
        name = name.toLowerCase();
        if ("admin".equals(name) && adminPassword.equals(password)) {
            return createResponse("Success", "Admin logged in successfully", HttpStatus.OK);
        } else {
            return createResponse("Error", "Invalid credentials", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<ResponseVO> createResponse(String status, String message, HttpStatus httpStatus)
    {
        ResponseVO responseVO = new ResponseVO(status, message, null);
        return new ResponseEntity<>(responseVO, httpStatus);
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<ResponseVO> deleteUser(@PathVariable(value = "id") long id)
    {
        return adminService.deleteUser(id);
    }
}
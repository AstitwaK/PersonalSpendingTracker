package com.PersonalSpendingTracker.controller;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.dto.ExpenseUpdateDto;
import com.PersonalSpendingTracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/{name}")
    public ResponseEntity<ResponseVO> dashboardView(@PathVariable String name,
                                                    @RequestParam(value = "startDate", required = false) String startDateStr,
                                                    @RequestParam(value = "endDate", required = false) String endDateStr) {
        return expenseService.findAllExpenses(name, startDateStr, endDateStr);
    }

    @PostMapping("/{name}/add")
    public ResponseEntity<ResponseVO> addExpense(@PathVariable String name,
                                                 @Valid @RequestBody ExpenseUpdateDto expenseUpdateDto) {
        return expenseService.addExpense(name, expenseUpdateDto);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ResponseVO> updateExpense(@PathVariable long id,
                                                    @Valid @RequestBody ExpenseUpdateDto expenseUpdateDto) {
        return expenseService.updateExpense(id, expenseUpdateDto);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<ResponseVO> deleteById(@PathVariable long id) {
        return expenseService.deleteById(id);
    }
}

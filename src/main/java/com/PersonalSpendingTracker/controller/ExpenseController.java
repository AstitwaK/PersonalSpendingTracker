package com.PersonalSpendingTracker.controller;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.dto.ExpenseUpdateDto;
import com.PersonalSpendingTracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/{name}")
    @ResponseBody
    public ResponseEntity<ResponseVO> dashboardView(@PathVariable(value = "name") String userName,
                                                   @RequestParam(value = "startDate", required = false) String startDateStr,
                                                   @RequestParam(value = "endDate", required = false) String endDateStr){
        return expenseService.findAllExpenses(userName,startDateStr,endDateStr);
    }

    @PostMapping("/{name}/add")
    @ResponseBody
    public ResponseEntity<ResponseVO> addExpense(@PathVariable(value = "name") String userName, @RequestBody ExpenseUpdateDto expenseUpdateDto) {
        return expenseService.addExpense(expenseUpdateDto.getExpName(), expenseUpdateDto.getDate(), expenseUpdateDto.getCostOfExp(), expenseUpdateDto.getQuantity());
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<ResponseVO> updateForm(@PathVariable(value = "id") long id,@RequestBody ExpenseUpdateDto expenseUpdateDto) {
        return expenseService.updateExpense(id,expenseUpdateDto.getExpName(), expenseUpdateDto.getDate(),
                expenseUpdateDto.getCostOfExp(), expenseUpdateDto.getQuantity(),expenseUpdateDto.getCreatedTimestamp());
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<ResponseVO> deleteById(@PathVariable(value = "id") long id) {
        return expenseService.deleteById(id);
    }
}

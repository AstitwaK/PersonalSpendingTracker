package com.PersonalSpendingTracker.controller;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.dto.ExpenseUpdateDto;
import com.PersonalSpendingTracker.model.Expense;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/{name}")
    @ResponseBody
    public ResponseVO dashboardview(@PathVariable(value = "name") String userName, @RequestParam(value = "startDate", required = false) String startDateStr,
                                    @RequestParam(value = "endDate", required = false) String endDateStr){
        return expenseService.findAllExpenses(userName,startDateStr,endDateStr);
    }

    @PostMapping("/{name}/add")
    @ResponseBody
    public ResponseVO addexpense(@PathVariable(value = "name") String userName,@RequestBody ExpenseUpdateDto expenseUpdateDto) {
        return expenseService.addById(expenseUpdateDto.getExpName(), expenseUpdateDto.getDate(), expenseUpdateDto.getCostOfExp(), expenseUpdateDto.getQuantity());
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseVO updateForm(@PathVariable(value = "id") long id,@RequestBody ExpenseUpdateDto expenseUpdateDto) {
        return expenseService.updateById(id,expenseUpdateDto.getExpName(), expenseUpdateDto.getDate(),
                expenseUpdateDto.getCostOfExp(), expenseUpdateDto.getQuantity(),expenseUpdateDto.getCreatedTimestamp());
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseVO deleteThroughId(@PathVariable(value = "id") long id) {
        return expenseService.deleteById(id);
    }
}

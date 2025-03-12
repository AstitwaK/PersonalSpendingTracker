package com.PersonalSpendingTracker.service;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.dto.ExpenseUpdateDto;
import com.PersonalSpendingTracker.exception.ExpenseNotFoundException;
import com.PersonalSpendingTracker.exception.UserNotFoundException;
import com.PersonalSpendingTracker.model.Expense;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.repository.ExpenseRepository;
import com.PersonalSpendingTracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class ExpenseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public User getByName(String userName) {
        return userRepository.findActiveUserByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found for username: " + userName));
    }

    public List<Expense> findAllExpenses(User user) {
        return expenseRepository.findByUser(user.getId());
    }

    public List<Expense> getExpensesByDateRange(Date startDate, Date endDate, User user) {
        return expenseRepository.findByDateBetweenAndUser(startDate, endDate, user.getId());
    }

    public double calculateTotalExpense(List<Expense> expenses) {
        return expenses.stream()
                .mapToDouble(expense -> expense.getCostOfExp() * expense.getQuantity())
                .sum();
    }

    public ResponseEntity<ResponseVO> findAllExpenses(String userName, String startDateStr, String endDateStr) {
        User user = getByName(userName);
        LocalDate startDate = parseDate(startDateStr);
        LocalDate endDate = parseDate(endDateStr);

        List<Expense> expenses = (startDate != null && endDate != null)
                ? getExpensesByDateRange(
                Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                user)
                : findAllExpenses(user);

        log.info("Expenses retrieved for user: {}", userName);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("expenses", expenses);
        responseData.put("totalExpense", calculateTotalExpense(expenses));

        return ResponseEntity.ok(new ResponseVO("Success", (startDate != null && endDate != null) ?
                "Expenses list found by Date" : "Expenses list found", responseData));
    }

    public ResponseEntity<ResponseVO> deleteById(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseNotFoundException("Expense not found with ID: " + id);
        }

        expenseRepository.deleteById(id);
        log.info("Successfully deleted expense with ID {}", id);
        return ResponseEntity.ok(new ResponseVO("Success", "Expense Deleted", null));
    }

    public ResponseEntity<ResponseVO> addExpense(String userName, ExpenseUpdateDto expenseDTO) {
        User user = getByName(userName);
        LocalDate parsedDate = parseDate(expenseDTO.getDate());
        Date expDate = Date.from(parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Expense expense = Expense.builder()
                .expName(expenseDTO.getExpName())
                .date(expDate)
                .costOfExp(expenseDTO.getCostOfExp())
                .quantity(expenseDTO.getQuantity())
                .user(user)
                .build();

        expenseRepository.save(expense);
        log.info("Expense successfully added: {}", expense);
        return ResponseEntity.ok(new ResponseVO("Success", "Expense Added", expense));
    }

    public ResponseEntity<ResponseVO> updateExpense(Long id, ExpenseUpdateDto expenseDTO) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with ID: " + id));

        if (expenseDTO.getExpName() != null) expense.setExpName(expenseDTO.getExpName());
        if (expenseDTO.getDate() != null) {
            LocalDate parsedDate = parseDate(expenseDTO.getDate());
            expense.setDate(Date.from(parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (expenseDTO.getCostOfExp() != null) expense.setCostOfExp(expenseDTO.getCostOfExp());
        if (expenseDTO.getQuantity() != null) expense.setQuantity(expenseDTO.getQuantity());
        if (expenseDTO.getCreatedTimestamp() != null) expense.setCreatedTimestamp(expenseDTO.getCreatedTimestamp());

        expenseRepository.save(expense);
        log.info("Expense updated successfully: {}", expense);
        return ResponseEntity.ok(new ResponseVO("Success", "Expense Updated", expense));
    }

    private LocalDate parseDate(String dateStr) {
        return (dateStr == null || dateStr.isBlank())
                ? LocalDate.of(2000, 1, 1)
                : LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-M-dd"));
    }
}

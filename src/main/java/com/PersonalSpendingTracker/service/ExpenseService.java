package com.PersonalSpendingTracker.service;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.dto.ExpenseUpdateDto;
import com.PersonalSpendingTracker.model.Expense;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.repository.ExpenseRepository;
import com.PersonalSpendingTracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Slf4j
public class ExpenseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseUpdateDto expenseUpdateDto;

    public User getByName(String userName) {
        return userRepository.findActiveUserByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found for username: " + userName));
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

        try {
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

            ResponseVO responseVO = new ResponseVO("Success", (startDate != null && endDate != null) ?
                    "Expenses list found by Date" : "Expenses list found", responseData);

            return ResponseEntity.ok(responseVO);
        } catch (DateTimeParseException e) {
            return handleDateError(startDateStr, endDateStr, e);
        } catch (Exception e) {
            return handleUnexpectedError(e);
        }
    }

    public ResponseEntity<ResponseVO> deleteById(Long id) {
        if (!expenseRepository.existsById(id)) {
            log.warn("Attempted to delete a non-existent expense with ID {}", id);
            return ResponseEntity.badRequest().body(new ResponseVO("Error", "Expense not found", null));
        }

        expenseRepository.deleteById(id);
        log.info("Successfully deleted expense with ID {}", id);
        return ResponseEntity.ok(new ResponseVO("Success", "Expense Deleted", null));
    }

    public ResponseEntity<ResponseVO> addExpense(String userName, ExpenseUpdateDto expenseDTO) {
        try {
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
        } catch (NumberFormatException e) {
            return handleNumberError(expenseDTO.getCostOfExp(), expenseDTO.getQuantity(), e);
        } catch (DateTimeParseException e) {
            return handleDateError(expenseDTO.getDate(), null, e);
        } catch (Exception e) {
            return handleUnexpectedError(e);
        }
    }

    public ResponseEntity<ResponseVO> updateExpense(Long id, ExpenseUpdateDto expenseDTO) {
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseVO("Error", "Expense not found", null));
        }

        Expense expense = optionalExpense.get();
        try {
            double cost = (expenseDTO.getCostOfExp() != null) ? expenseDTO.getCostOfExp() : expense.getCostOfExp();
            int quantity = (expenseDTO.getQuantity() != null) ? expenseDTO.getQuantity() : expense.getQuantity();
            LocalDate parsedDate = parseDate(expenseDTO.getDate());

            if (expenseDTO.getExpName() != null) expense.setExpName(expenseDTO.getExpName());
            if (expenseDTO.getDate() != null) expense.setDate(Date.from(parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            expense.setCostOfExp(cost);
            expense.setQuantity(quantity);
            if (expenseDTO.getCreatedTimestamp() != null) expense.setCreatedTimestamp(expenseDTO.getCreatedTimestamp());

            expenseRepository.save(expense);
            log.info("Expense updated successfully: {}", expense);
            return ResponseEntity.ok(new ResponseVO("Success", "Expense Updated", expense));
        } catch (Exception e) {
            return handleUnexpectedError(e);
        }
    }

    private LocalDate parseDate(String dateStr) {
        return (dateStr == null || dateStr.isBlank())
                ? LocalDate.of(2000, 1, 1)
                : LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-M-dd"));
    }

    private ResponseEntity<ResponseVO> handleDateError(String startDateStr, String endDateStr, DateTimeParseException e) {
        log.error("Invalid date format: startDate={}, endDate={}", startDateStr, endDateStr, e);
        return ResponseEntity.badRequest().body(new ResponseVO("Error", "Invalid date format. Expected yyyy-M-dd", null));
    }

    private ResponseEntity<ResponseVO> handleNumberError(double costOfExp, int quantity, NumberFormatException e) {
        log.error("Invalid cost or quantity format: {}, {}", costOfExp, quantity, e);
        return ResponseEntity.badRequest().body(new ResponseVO("Error", "Invalid cost or quantity format", null));
    }

    private ResponseEntity<ResponseVO> handleUnexpectedError(Exception e) {
        log.error("Unexpected error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseVO("Error", "An unexpected error occurred", null));
    }
}
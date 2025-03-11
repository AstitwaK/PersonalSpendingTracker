package com.PersonalSpendingTracker.service;

import com.PersonalSpendingTracker.VO.ResponseVO;
import com.PersonalSpendingTracker.model.Expense;
import com.PersonalSpendingTracker.model.User;
import com.PersonalSpendingTracker.repository.ExpenseRepository;
import com.PersonalSpendingTracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    // Method to fetch user by username
    public User getByName(String userName) {
        return userRepository.findActiveUserByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found for id: " + userName));
    }

    // Fetch expenses for the given user
    public List<Expense> findAllExpenses(User user) {
        return expenseRepository.findByUser(user.getId());
    }

    // Method to handle getting expenses by date range
    public List<Expense> getExpensesByDateRange(Date startDate, Date endDate, User user) {
        return expenseRepository.findByDateBetweenAndUser(startDate, endDate, user.getId());
    }

    // Method to calculate the total expense
    public double calculateTotalExpense(List<Expense> expenses) {
        return expenses.stream()
                .mapToDouble(expense -> expense.getCostOfExp() * expense.getQuantity())
                .sum();
    }

    // Main method for finding all expenses, optionally filtering by date range
    public ResponseEntity<ResponseVO> findAllExpenses(String userName, String startDateStr, String endDateStr) {
        User user = getByName(userName);

        try {
            // Parse dates safely
            LocalDate startDate = (startDateStr != null && !startDateStr.isBlank())
                    ? LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern("yyyy-M-dd"))
                    : null;

            LocalDate endDate = (endDateStr != null && !endDateStr.isBlank())
                    ? LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("yyyy-M-dd"))
                    : null;

            List<Expense> expenses = (startDate != null && endDate != null)
                    ? getExpensesByDateRange(
                    Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    user)
                    : findAllExpenses(user);

            log.info("Expenses retrieved for user: {}", userName);

            // Calculate total expense
            double totalExpense = calculateTotalExpense(expenses);

            // Create a response map
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("expenses", expenses);
            responseData.put("totalExpense", totalExpense);

            ResponseVO responseVO = new ResponseVO("Success", (startDate != null && endDate != null) ?
                    "Expenses list found by Date" : "Expenses list found", responseData, HttpStatus.OK);

            return ResponseEntity.ok(responseVO);

        } catch (DateTimeParseException e) {
            log.error("Invalid date format: startDate={}, endDate={}", startDateStr, endDateStr, e);
            ResponseVO responseVO = new ResponseVO("Error", "Invalid date format. Expected yyyy-M-dd", null, HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(responseVO);
        } catch (Exception e) {
            log.error("Unexpected error retrieving expenses: {}", e.getMessage());
            ResponseVO responseVO = new ResponseVO("Error", "An unexpected error occurred", null, HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseVO);
        }
    }

    // Method to delete expense by ID
    public ResponseEntity<ResponseVO> deleteById(Long id) {
        if (!expenseRepository.existsById(id)) {
            log.warn("Attempted to delete a non-existent expense with ID {}", id);
            ResponseVO responseVO = new ResponseVO("Error", "Expense not found", null, HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(responseVO);
        }

        expenseRepository.deleteById(id);
        log.info("Successfully deleted expense with ID {}", id);
        ResponseVO responseVO = new ResponseVO("Success", "Expense Deleted", null, HttpStatus.OK);
        return ResponseEntity.ok(responseVO);
    }

    // Method to add a new expense
    public ResponseEntity<ResponseVO> addExpense(String expName, String date, String costOfExp, String quantity) {
        try {
            User user = getByName(expName);
            float cost = Float.parseFloat(costOfExp);
            int quant = Integer.parseInt(quantity);

            // Parse date safely
            LocalDate parsedDate = (date == null || date.isBlank())
                    ? LocalDate.of(2000, 1, 1)  // Fallback date
                    : LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-M-dd"));

            Date expDate = Date.from(parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            Expense expense = new Expense(null, expName, expDate, cost, quant, null, null, user);
            expenseRepository.save(expense);

            log.info("Expense successfully added: {}", expense);
            ResponseVO responseVO = new ResponseVO("Success", "Expense Added", expense, HttpStatus.OK);
            return ResponseEntity.ok(responseVO);
        } catch (NumberFormatException e) {
            log.error("Invalid cost or quantity format: {}, {}", costOfExp, quantity, e);
            ResponseVO responseVO = new ResponseVO("Error", "Invalid cost or quantity format", null, HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(responseVO);
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: {}", date, e);
            ResponseVO responseVO = new ResponseVO("Error", "Invalid date format. Expected yyyy-M-dd", null, HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(responseVO);
        } catch (Exception e) {
            log.error("Unexpected error while adding expense: {}", e.getMessage());
            ResponseVO responseVO = new ResponseVO("Error", "An unexpected error occurred", null, HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseVO);
        }
    }

    // Method to update an existing expense
    public ResponseEntity<ResponseVO> updateExpense(Long id, String expName, String expDate, String expCost, String expQuantity, Instant createdTimestamp) {
        Optional<Expense> optionalExpense = expenseRepository.findById(id);

        if (optionalExpense.isEmpty()) {
            log.warn("Attempted to update a non-existent expense with ID {}", id);
            ResponseVO responseVO = new ResponseVO("Error", "Expense not found", null, HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(responseVO);
        }

        Expense expense = optionalExpense.get();
        User user = expense.getUser();

        try {
            float cost = expCost != null ? Float.parseFloat(expCost) : expense.getCostOfExp();
            int quantity = expQuantity != null ? Integer.parseInt(expQuantity) : expense.getQuantity();

            LocalDate parsedDate = (expDate == null || expDate.isBlank())
                    ? LocalDate.of(2000, 1, 1)
                    : LocalDate.parse(expDate, DateTimeFormatter.ofPattern("yyyy-M-dd"));

            // Update only if new values are provided
            if (expName != null) expense.setExpName(expName);
            if (expDate != null) expense.setDate(Date.from(parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            expense.setCostOfExp(cost);
            expense.setQuantity(quantity);
            if (createdTimestamp != null) expense.setCreatedTimestamp(createdTimestamp);

            expenseRepository.save(expense);
            log.info("Expense updated successfully: {}", expense);
            ResponseVO responseVO = new ResponseVO("Success", "Expense Updated", expense, HttpStatus.OK);
            return ResponseEntity.ok(responseVO);
        } catch (Exception e) {
            log.error("Error updating expense with ID {}: {}", id, e.getMessage());
            ResponseVO responseVO = new ResponseVO("Error", "Invalid input data", null, HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseVO);
        }
    }
}
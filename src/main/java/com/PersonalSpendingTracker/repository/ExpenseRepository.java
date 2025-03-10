package com.PersonalSpendingTracker.repository;

import com.PersonalSpendingTracker.model.Expense;
import com.PersonalSpendingTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByuser(User user);
    Optional<Expense> findById(Long id);
    List<Expense> findByDateBetweenAndUser(Date startDate, Date endDate,User user);
}

package com.PersonalSpendingTracker.repository;

import com.PersonalSpendingTracker.model.Expense;
import com.PersonalSpendingTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>
{

    @Query(value = "SELECT * FROM expense WHERE user_id = :userId", nativeQuery = true)
    List<Expense> findByUser(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM expense WHERE date BETWEEN :startDate AND :endDate AND user_id = :userId", nativeQuery = true)
    List<Expense> findByDateBetweenAndUser(@Param("startDate") Date startDate,
                                           @Param("endDate") Date endDate,
                                           @Param("userId") Long userId);

}

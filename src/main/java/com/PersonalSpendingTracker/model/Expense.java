package com.PersonalSpendingTracker.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "expense")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "exp_name")
    private String expName;
    @Column(name = "exp_date")
    private Date date;
    @Column(name = "exp_cost")
    private float costOfExp;
    @Column(name = "exp_quantity")
    private int quantity;
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdTimestamp;
    @UpdateTimestamp
    @Column(name = "last_updated")
    private Instant lastUpdatedTimestamp;
    @ManyToOne
    @JoinColumn(name = "user_name",nullable = false)
    private User user;
}

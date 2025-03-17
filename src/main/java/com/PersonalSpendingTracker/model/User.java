package com.PersonalSpendingTracker.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(name = "pst_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_name",unique = true)
    private String userName;
    @Column(name = "email",unique = true)
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "phone",unique = true)
    private String phone;
    @Column(name = "status", columnDefinition = "boolean default true")
    private boolean status;
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdTimestamp;
    @UpdateTimestamp
    @Column(name = "last_updated")
    private Instant lastUpdatedTimestamp;

}

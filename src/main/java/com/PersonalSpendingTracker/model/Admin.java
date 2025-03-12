package com.PersonalSpendingTracker.model;

import jakarta.persistence.*;
import lombok.*;

/*TODO
*  1. If you are not storing any Admin details in db, what it is the purpose of creating the entity and repository ?
*   Admin and Admin Repository can be deleted*/

@Entity
@Table(name = "admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "admin_name")
    private String adminName;
    @Column(name = "admin_password")
    private String adminPassword;
}

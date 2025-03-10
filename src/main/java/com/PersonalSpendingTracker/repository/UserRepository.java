package com.PersonalSpendingTracker.repository;

import com.PersonalSpendingTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findBystatusTrue();

    Optional<User> findByemail(String email);

    Optional<User> findByphone(String phone);

    Optional<User> findById(Long id);

    Optional<User> findByUserNameAndStatusTrue(String userName);
}

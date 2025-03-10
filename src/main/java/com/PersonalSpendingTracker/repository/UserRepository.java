package com.PersonalSpendingTracker.repository;

import com.PersonalSpendingTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /*TODO
     *  1. write native queries to get all the details in every method
     *   e.g. -> @Query(value = "select * from user where status = true", nativeQuery = true)
     *  2. maintain naming convention e.g. -> getAllActiveUsers, findByEmail
     * */

    @Query(value = "SELECT * FROM users WHERE status = true", nativeQuery = true)
    List<User> getAllActiveUsers();

    @Query(value = "SELECT * FROM users WHERE phone = ?1 LIMIT 1", nativeQuery = true)
    Optional<User> findByPhone(String phone);

    @Query(value = "SELECT * FROM users WHERE id = ?1 LIMIT 1", nativeQuery = true)
    Optional<User> findById(Long id);

    @Query(value = "SELECT * FROM users WHERE (username = ?1 OR email = ?2) AND status = true LIMIT 1", nativeQuery = true)
    Optional<User> findByUsernameOrEmailAndStatusTrue(String username, String email);

    @Query(value = "SELECT * FROM users WHERE username = ?1 AND status = true LIMIT 1", nativeQuery = true)
    Optional<User> findByUsernameAndStatusTrue(String username);
}

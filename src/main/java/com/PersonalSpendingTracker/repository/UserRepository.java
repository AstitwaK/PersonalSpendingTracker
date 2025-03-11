package com.PersonalSpendingTracker.repository;

import com.PersonalSpendingTracker.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "SELECT * FROM users WHERE phone = :phone", nativeQuery = true)
    Optional<User> findByPhone(@Param("phone") String phone);

 /*   @Query(value = "SELECT * FROM users WHERE id = ?1 LIMIT 1", nativeQuery = true)
    Optional<User> findById(Long id);*/

    @Query(value = "SELECT * FROM users WHERE (username = :userName OR email = :email) AND status = true", nativeQuery = true)
    Optional<User> findActiveUserByUserNameAndEmail(@Param("userName") String userName, @Param("email") String email);

    @Query(value = "SELECT * FROM users WHERE username = :userName AND status = true", nativeQuery = true)
    Optional<User> findActiveUserByUserName(@Param("userName") String userName);
}

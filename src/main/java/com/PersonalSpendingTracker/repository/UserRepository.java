package com.PersonalSpendingTracker.repository;

import com.PersonalSpendingTracker.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long>
{

    @Query(value = "SELECT * FROM pst_user WHERE status = true", nativeQuery = true)
    List<User> getAllActiveUsers();

    @Query(value = "SELECT * FROM pst_user WHERE phone = :phone", nativeQuery = true)
    Optional<User> findByPhone(@Param("phone") String phone);

    @Query(value = "SELECT * FROM pst_user WHERE (username = :userName OR email = :email) AND status = true", nativeQuery = true)
    Optional<User> findActiveUserByUserNameAndEmail(@Param("userName") String userName, @Param("email") String email);

    @Query(value = "SELECT * FROM pst_user WHERE username = :userName AND status = true", nativeQuery = true)
    Optional<User> findActiveUserByUserName(@Param("userName") String userName);
}

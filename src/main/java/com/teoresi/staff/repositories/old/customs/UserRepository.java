package com.teoresi.staff.repositories.old.customs;

import com.teoresi.staff.entities.old.User;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Transactional
    @Query("SELECT u FROM User u JOIN u.resource r WHERE r.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Transactional
    @Query("SELECT u FROM User u JOIN u.resource r WHERE r.id = :resourceId")
    Optional<User> findByResourceId(Long resourceId);

}

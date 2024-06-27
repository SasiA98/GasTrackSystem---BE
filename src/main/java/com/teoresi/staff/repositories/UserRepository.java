package com.teoresi.staff.repositories;

import com.teoresi.staff.entities.ResourceHourlyCost;
import com.teoresi.staff.entities.User;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import com.teoresi.staff.shared.models.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Transactional
    @Query("SELECT u FROM User u JOIN u.resource r WHERE r.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Transactional
    @Query("SELECT u FROM User u JOIN u.resource r WHERE r.id = :resourceId")
    Optional<User> findByResourceId(Long resourceId);

}

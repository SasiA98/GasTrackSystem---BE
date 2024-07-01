package com.teoresi.staff.repositories.old.customs;

import com.teoresi.staff.entities.old.PasswordResetToken;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PasswordTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    List<PasswordResetToken> findByExpiryDateBefore(Date now);
    PasswordResetToken findByToken(String Token);
}

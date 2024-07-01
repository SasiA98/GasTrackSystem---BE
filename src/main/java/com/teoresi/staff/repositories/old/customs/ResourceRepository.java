package com.teoresi.staff.repositories.old.customs;

import com.teoresi.staff.entities.old.Resource;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface ResourceRepository extends CrudRepository<Resource, Long> {

    @Transactional
    @Query("SELECT r FROM resource r JOIN r.unit u WHERE u.id = :unitId")
    List<Resource> findByUnitId(Long unitId);

    @Query("SELECT r FROM resource r WHERE r.employeeId = :employeeId")
    Optional<Resource> findByEmployeeId(Integer employeeId);

    Optional<Resource> findByNameAndSurname(String name, String surname);
}

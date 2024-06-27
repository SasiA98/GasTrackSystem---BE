package com.teoresi.staff.repositories;

import com.teoresi.staff.entities.ResourceSkill;
import com.teoresi.staff.entities.ResourceSkillKey;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ResourceSkillRepository extends CrudRepository<ResourceSkill, ResourceSkillKey> {
    @Transactional
    @Query("SELECT s FROM resource_skill s WHERE resource_id = :id")
    List<ResourceSkill> findAllByResourceId(Long id);
}

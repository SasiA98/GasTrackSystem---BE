package com.teoresi.staff.repositories;

import com.teoresi.staff.entities.Skill;
import com.teoresi.staff.entities.SkillGroup;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillGroupRepository extends CrudRepository<SkillGroup, Long> {
}

package com.teoresi.staff.repositories.old.customs;

import com.teoresi.staff.entities.old.Skill;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends CrudRepository<Skill, Long> {
}

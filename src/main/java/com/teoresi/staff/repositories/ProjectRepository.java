package com.teoresi.staff.repositories;

import com.teoresi.staff.entities.Allocation;
import com.teoresi.staff.entities.Project;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    @Transactional
    @Query("SELECT A FROM allocation A WHERE project_id = :id")
    List<Allocation> getAllocation(Long id);

    Optional<Project> findProjectByProjectId(String projectId);
}

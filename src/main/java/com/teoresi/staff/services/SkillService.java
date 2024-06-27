package com.teoresi.staff.services;

import com.teoresi.staff.components.SkillSpecificationsFactory;
import com.teoresi.staff.entities.ResourceSkill;
import com.teoresi.staff.entities.Skill;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.repositories.ResourceSkillRepository;
import com.teoresi.staff.repositories.SkillRepository;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.shared.services.BasicService;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService extends BasicService {

    private final SkillRepository skillRepository;
    private final ResourceSkillRepository resourceSkillRepository;
    private final SkillSpecificationsFactory skillSpecificationsFactory;
    private final Logger logger = LoggerFactory.getLogger(SkillService.class);
    private static final String SKILL_ID_NOT_ERASABLE = "Skill \"%s\" not erasable. There are one or more associated skills";
    private static final String SKILL_ID_NOT_FOUND = "Skill with id %d not found.";

    public SkillService(SkillRepository skillRepository, ResourceSkillRepository resourceSkillRepository, SessionService sessionService, SkillSpecificationsFactory skillSpecificationsFactory) {
        super(sessionService, LoggerFactory.getLogger(SkillService.class));
        this.skillRepository = skillRepository;
        this.resourceSkillRepository = resourceSkillRepository;
        this.skillSpecificationsFactory = skillSpecificationsFactory;
    }


    public Skill create(Skill skill) {
        skill.setId(null);
        return save(skillRepository, skill);
    }

    public Skill update(Skill skill) {
        if (!skillRepository.existsById(skill.getId())) {
            throw buildEntityWithIdNotFoundException(skill.getId(), SKILL_ID_NOT_FOUND);
        }
        return save(skillRepository, skill);
    }

    private Skill save(Skill skill) {
        try {
            if(sessionService.getCurrentUser() != null) logger.info(USER_DESCRIPTION, sessionService.getCurrentUser().getUsername());

            logger.info(SAVING_ENTITY, skill.toString());
            skill = skillRepository.save(skill);
            logger.info(SAVED_ENTITY);
            return skill;

        } catch (JpaSystemException ex) {
            Throwable cause = ex.getCause();
            String errorMessage = cause.getMessage();
            String errorState;

            if (cause instanceof GenericJDBCException) {
                errorState = ((GenericJDBCException) cause).getSQLException().getSQLState();

                if (errorState.equals(USER_DEFINED_SQL_EXCEPTION))
                    errorMessage = ((GenericJDBCException) cause).getSQLException().getMessage();
                else if(errorState.equals(COULD_NOT_EXECUTE_STATEMENT_ERROR)){
                    errorMessage = ((GenericJDBCException) cause).getSQLException().getMessage();
                }
            }
            logger.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        } catch (DataIntegrityViolationException ex) {
            String message = CONSTRAINT_VIOLATION;

            if(ex.getCause() != null) {
                String detailedMessage = ex.getCause().getCause().getMessage();
                if (detailedMessage.contains("name_unique"))
                    message = "This name has already been associated with another skill";
            }
            logger.error(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);

        }
    }

    public Page<Skill> searchAdvanced(Optional<Filter<Skill>> filter, Pageable pageable) {
        try {
            return filter.map(resourceFilter ->
                    skillRepository.findAll(getSpecificationForAdvancedSearch(resourceFilter), pageable)
            ).orElseGet(() -> skillRepository.findAll(pageable));
        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Specification<Skill> getSpecificationForAdvancedSearch(Filter<Skill> skillFilter){
        return skillFilter.toSpecification(skillSpecificationsFactory);
    }

    public List<ResourceSkill> getAllByResourceId(Long id) {
        return resourceSkillRepository.findAllByResourceId(id);
    }

    public Skill getById(Long id) {
        return getById(skillRepository, id, SKILL_ID_NOT_FOUND);
    }

    public void deleteById(Long id) {
        Skill skill = getById(skillRepository, id, SKILL_ID_NOT_FOUND);
        try {
            skillRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw buildSkillWithIdNotErasableException(skill.getName());
        }
    }

    public List<Skill> getAll(){
        return getAll(skillRepository);
    }

    private ResponseStatusException buildSkillWithIdNotErasableException(String nameSkillGroup) {
        String message = String.format(SKILL_ID_NOT_ERASABLE, nameSkillGroup);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}

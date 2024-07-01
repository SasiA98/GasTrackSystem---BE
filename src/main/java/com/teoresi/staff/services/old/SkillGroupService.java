package com.teoresi.staff.services.old;

import com.teoresi.staff.entities.old.SkillGroup;
import com.teoresi.staff.repositories.old.customs.SkillGroupRepository;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.shared.services.BasicService;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SkillGroupService extends BasicService {

    private final SkillGroupRepository skillGroupRepository;
    private final Logger logger = LoggerFactory.getLogger(SkillGroupService.class);
    private static final String SKILL_GROUP_ID_NOT_FOUND = "Skill Group with id %d not found.";
    private static final String SKILL_GROUP_ID_NOT_ERASABLE = "Skill Group \"%s\" not erasable. There are one or more associated resources";

    public SkillGroupService(SkillGroupRepository skillGroupRepository, SessionService sessionService) {
        super(sessionService, LoggerFactory.getLogger(SkillGroupService.class));
        this.sessionService = sessionService;
        this.skillGroupRepository = skillGroupRepository;
    }


    public SkillGroup create(SkillGroup skillGroup) {
        skillGroup.setId(null);
        return save(skillGroup);
    }

    public SkillGroup update(SkillGroup skillGroup) {
        if (!skillGroupRepository.existsById(skillGroup.getId())) {
            throw buildEntityWithIdNotFoundException(skillGroup.getId(), SKILL_GROUP_ID_NOT_FOUND);
        }
        return save(skillGroup);
    }

    private SkillGroup save(SkillGroup skillGroup) {
        try {
            if(sessionService.getCurrentUser() != null) logger.info(USER_DESCRIPTION, sessionService.getCurrentUser().getUsername());

            logger.info(SAVING_ENTITY, skillGroup.toString());
            skillGroup = skillGroupRepository.save(skillGroup);
            logger.info(SAVED_ENTITY);
            return skillGroup;

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
                    message = "This name has already been associated with another skill group";
            }
            logger.error(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);

        }
    }

    public SkillGroup getById(Long id) {
        return getById(skillGroupRepository, id, SKILL_GROUP_ID_NOT_FOUND);
    }

    public void deleteById(Long id) {
        SkillGroup skillGroup = getById(skillGroupRepository, id, SKILL_GROUP_ID_NOT_FOUND);
        try {
            skillGroupRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
           throw buildSkillGroupWithIdNotErasableException(skillGroup.getName());
        }
    }

    public List<SkillGroup> getAll(){
        return getAll(skillGroupRepository);
    }

    private ResponseStatusException buildSkillGroupWithIdNotErasableException(String nameSkillGroup) {
        String message = String.format(SKILL_GROUP_ID_NOT_ERASABLE, nameSkillGroup);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }


}

package com.client.staff.shared.services;

import com.client.staff.libs.data.models.IdentifiableEntity;
import com.client.staff.libs.data.repositories.CrudRepository;
import com.client.staff.security.services.SessionService;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public abstract class BasicService {

    protected static final String CONSTRAINT_VIOLATION = "One or more constraint violations occurred. %s";
    protected static final String USER_DEFINED_SQL_EXCEPTION = "45000";
    protected static final String COULD_NOT_EXECUTE_STATEMENT_ERROR = "HY000";
    protected static final String INVALID_SEARCH_CRITERIA = "Invalid search criteria: %s";
    protected static final String SAVING_ENTITY = "Saving entity: {}";
    protected static final String SAVED_ENTITY = "Entity saved successfully";
    protected static final String USER_DESCRIPTION = "User : {}";
    protected static final String ARCHIVE_DIRECTORY = "archive/";


    protected SessionService sessionService;
    protected final Logger logger;


    public BasicService(SessionService sessionService, Logger logger) {
        this.sessionService = sessionService;
        this.logger = logger;
    }


    protected  <T extends IdentifiableEntity<K>,K> T save (CrudRepository<T,K> repository, T entity){
        try {
            if(sessionService.getCurrentUser() != null) logger.info(USER_DESCRIPTION, sessionService.getCurrentUser().getUsername());

            logger.info(SAVING_ENTITY, entity.toString());
            entity = repository.save(entity);
            logger.info(SAVED_ENTITY);
            return entity;

        } catch (JpaSystemException ex) {

            String errorMessage = handleJpaException(ex);
            logger.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        } catch (DataIntegrityViolationException ex) {
            String errorMessage = handleDataIntegrityViolationException(ex);
            logger.error(errorMessage);

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    protected String handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return CONSTRAINT_VIOLATION;
    }

    protected String handleJpaException(JpaSystemException ex){
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

        return errorMessage;
    }

    protected <T extends IdentifiableEntity<K>, K> T getById(CrudRepository<T, K> repository, K id, String msg) {
        Optional<T> entity = repository.findById(id);
        if (entity.isEmpty())
            throw buildEntityWithIdNotFoundException(id, msg);

        return entity.get();
    }

    protected <T extends IdentifiableEntity<K>, K> void deleteById(CrudRepository<T, K> repository, K id, String msg) {
        try {
            if (sessionService != null) logger.info("User : {}", sessionService.getCurrentUser().getUsername());

            logger.info("Deleting entity: {}", id);
            repository.deleteById(id);
            logger.info("Entity deleted successfully");

        } catch (DataIntegrityViolationException e) {
            throw buildEntityWithIdNotFoundException(id, msg);
        }
    }

    protected <T extends IdentifiableEntity<K>, K> List<T> getAll(CrudRepository<T, K> repository) {
        return repository.findAll();
    }

    protected LocalDate getLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    protected <K> ResponseStatusException buildEntityWithIdNotFoundException(K id, String msg) {
        String message = String.format(msg, id);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}

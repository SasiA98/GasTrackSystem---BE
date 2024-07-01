package com.teoresi.staff.services.old;

import com.teoresi.staff.components.old.UnitSpecificationsFactory;
import com.teoresi.staff.entities.old.Resource;
import com.teoresi.staff.entities.old.Unit;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.repositories.old.customs.UnitRepository;
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

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
public class UnitService extends BasicService {

    private final UnitRepository unitRepository;
    private final UnitSpecificationsFactory unitSpecificationsFactory;

    private final Logger logger = LoggerFactory.getLogger(UnitService.class);
    private static final String UNIT_ID_NOT_FOUND = "Unit with id %d not found.";
    private static final String UNIT_TRIGRAM_NOT_FOUND = "Unit with trigram %S not found.";

    public UnitService(UnitRepository unitRepository, UnitSpecificationsFactory unitSpecificationsFactory, SessionService sessionService) {
        super(sessionService, LoggerFactory.getLogger(UnitService.class));
        this.unitRepository = unitRepository;
        this.unitSpecificationsFactory = unitSpecificationsFactory;
    }


    public Unit create(Unit unit) {
        unit.setId(null);
        return save(unit);
    }

    public Unit update(Unit unit) {
        if (!unitRepository.existsById(unit.getId())) {
            throw buildEntityWithIdNotFoundException(unit.getId(), UNIT_ID_NOT_FOUND);
        }
        return save(unit);
    }

    private Unit save(Unit unit) {
        try {
            if(sessionService.getCurrentUser() != null) logger.info(USER_DESCRIPTION, sessionService.getCurrentUser().getUsername());

            logger.info(SAVING_ENTITY, unit.toString());
            unit = unitRepository.save(unit);
            logger.info(SAVED_ENTITY);
            return unit;

        } catch (JpaSystemException ex) {
            Throwable cause = ex.getCause();
            String errorMessage = cause.getMessage();
            String errorState;

            if (cause instanceof GenericJDBCException) {
                errorState = ((GenericJDBCException) cause).getSQLException().getSQLState();

                if (errorState.equals(USER_DEFINED_SQL_EXCEPTION))
                    errorMessage = ((GenericJDBCException) cause).getSQLException().getMessage();
                else if (errorState.equals(COULD_NOT_EXECUTE_STATEMENT_ERROR)) {
                    errorMessage = ((GenericJDBCException) cause).getSQLException().getMessage();
                }
            }
            logger.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        } catch (DataIntegrityViolationException ex) {
            String message = CONSTRAINT_VIOLATION;

            if (ex.getCause() != null) {
                String detailedMessage = ex.getCause().getCause().getMessage();
                if (detailedMessage.contains("trigram_unique"))
                    message = "This trigram has already been associated with another unit";
            }
            logger.error(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    public Unit getById(Long id) {
        return getById(unitRepository, id, UNIT_ID_NOT_FOUND);
    }

    public void deleteById(Long id) {
        deleteById(unitRepository, id, UNIT_ID_NOT_FOUND);
    }

    public List<Unit> getAll(){
        return getAll(unitRepository);
    }

    public Unit getByTrigram(String trigram, boolean isFromImport) {
        Optional<Unit> unit = unitRepository.findByTrigram(trigram);
        if (unit.isEmpty())
            if(isFromImport) {
                String message = "The unit trigram " + trigram + " does not exist";
                throw new IllegalArgumentException(message);
            }else
                throw buildUnitWithTrigramNotFoundException(trigram);

        return unit.get();
    }

    public List<Resource> getActiveResourcesInLastSixMonthsById(Long id) {

        LocalDate minDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).minusMonths(5);
        return unitRepository.findActiveResourcesInLastSixMonthsById(id, minDate);
    }

    public List<Resource> getActiveResourcesById(Long id) {
        return unitRepository.findActiveResourcesById(id);
    }


    public Page<Unit> searchAdvanced(Optional<Filter<Unit>> filter, Pageable pageable) {
        try {
            return filter.map(resourceFilter ->
                    unitRepository.findAll(getSpecificationForAdvancedSearch(resourceFilter), pageable)
            ).orElseGet(() -> unitRepository.findAll(pageable));
        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Specification<Unit> getSpecificationForAdvancedSearch(Filter<Unit> unitFilter){
        return unitFilter.toSpecification(unitSpecificationsFactory);
    }

    private ResponseStatusException buildUnitWithTrigramNotFoundException(String trigram) {
        String message = String.format(UNIT_TRIGRAM_NOT_FOUND, trigram);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

}

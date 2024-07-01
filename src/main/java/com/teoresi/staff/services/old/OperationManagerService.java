package com.teoresi.staff.services.old;

import com.teoresi.staff.components.old.TrigramSpecificationsFactory;
import com.teoresi.staff.entities.old.OperationManager;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.repositories.old.customs.OperationManagerRepository;
import com.teoresi.staff.security.models.JwtAuthentication;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.shared.services.BasicService;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class OperationManagerService extends BasicService {

    private final OperationManagerRepository operationManagerRepository;
    private final SessionService sessionService;
    private final TrigramSpecificationsFactory trigramSpecificationsFactory;

    private static final String OPERATIONS_TRIGRAM_ID_NOT_FOUND = "Operations trigrams with id %d not found";

    public OperationManagerService(OperationManagerRepository operationManagerRepository, SessionService sessionService, TrigramSpecificationsFactory trigramSpecificationsFactory) {
        super(sessionService, LoggerFactory.getLogger(OperationManagerService.class));
        this.operationManagerRepository = operationManagerRepository;
        this.sessionService = sessionService;
        this.trigramSpecificationsFactory = trigramSpecificationsFactory;
    }

    public OperationManager create(OperationManager operationManager) {
        operationManager.setId(null);
        operationManager = save(operationManagerRepository, operationManager);

        return operationManager;
    }

    public OperationManager update(OperationManager operationManager) {

        if(!operationManagerRepository.existsById(operationManager.getId())) {
            throw buildEntityWithIdNotFoundException(operationManager.getId(), OPERATIONS_TRIGRAM_ID_NOT_FOUND);
        }
        
        operationManager = save(operationManagerRepository, operationManager);

        return operationManager;
    }

    public void deleteById(Long id) {
        deleteById(operationManagerRepository, id, OPERATIONS_TRIGRAM_ID_NOT_FOUND);
    }

    public List<OperationManager> getAll(){
        return getAll(operationManagerRepository);
    }

    public OperationManager getById(Long id) {
        return getById(operationManagerRepository, id, OPERATIONS_TRIGRAM_ID_NOT_FOUND);
    }

    public Page<OperationManager> searchAdvanced(Optional<Filter<OperationManager>> filter, Pageable pageable) {
        try {
            JwtAuthentication jwtAuthentication = sessionService.getCurrentUser();
            return filter.map(operationsTrigramsFilter ->
                    operationManagerRepository.findAll(getSpecificationForAdvancedSearch(jwtAuthentication, operationsTrigramsFilter), pageable)
            ).orElseGet(() -> operationManagerRepository.findAll(pageable));
        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Specification<OperationManager> getSpecificationForAdvancedSearch(JwtAuthentication jwtAuthentication,
                                                                              Filter<OperationManager> operationsTrigramsFilter){
        return operationsTrigramsFilter.toSpecification(trigramSpecificationsFactory);
    }





}

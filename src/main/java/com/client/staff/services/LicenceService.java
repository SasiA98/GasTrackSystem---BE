package com.client.staff.services;

import com.client.staff.components.LicenceSpecificationsFactory;
import com.client.staff.entities.Licence;
import com.client.staff.libs.data.models.Filter;
import com.client.staff.repositories.LicenceRepository;
import com.client.staff.security.services.SessionService;
import com.client.staff.shared.services.BasicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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
public class LicenceService extends BasicService {

    private final LicenceRepository licenceRepository;
    private final LicenceSpecificationsFactory licenceSpecificationsFactory;
    private final Logger logger = LoggerFactory.getLogger(LicenceService.class);
    private static final String LICENCE_ID_NOT_FOUND = "Licenza con id %d non trovata";
    private static final String LICENCE_NAME_UNIQUE_ERROR = "Esiste una licenza con lo stesso nome";

    public LicenceService(SessionService sessionService, LicenceRepository licenceRepository, LicenceSpecificationsFactory companySpecificationsFactory) {
        super(sessionService, LoggerFactory.getLogger(LicenceService.class));
        this.licenceRepository = licenceRepository;
        this.licenceSpecificationsFactory = companySpecificationsFactory;
    }

    public Licence create(Licence licence) {
        licence.setId(null);
        String fileName = Licence.computeDirectory(licence);
        licence.setDirectory(fileName);
        return save(licenceRepository, licence);
    }

    public Licence update(Licence licence) {
        Licence storedLicence = getById(licence.getId());
        licence.setDirectory(storedLicence.getDirectory());
        return save(licenceRepository, licence);
    }

    @Override
    protected String handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = CONSTRAINT_VIOLATION;

        if (ex.getCause() != null) {
            String detailedMessage = ex.getCause().getCause().getMessage();
            if (detailedMessage.contains("licence_name_unique")) {
                message = LICENCE_NAME_UNIQUE_ERROR;
            }
        }
        return message;
    }


    public Licence getById(Long id) {
        return getById(licenceRepository, id, LICENCE_ID_NOT_FOUND);
    }

    public void deleteById(Long id) {
        deleteById(licenceRepository, id, LICENCE_ID_NOT_FOUND);
    }

    public List<Licence> getAll(){
        return getAll(licenceRepository);
    }


    public Page<Licence> searchAdvanced(Optional<Filter<Licence>> filter, Pageable pageable) {
        try {
            return filter.map(licenceFilter ->
                    licenceRepository.findAll(getSpecificationForAdvancedSearch(licenceFilter), pageable)
            ).orElseGet(() -> licenceRepository.findAll(pageable));
        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Specification<Licence> getSpecificationForAdvancedSearch(Filter<Licence> companyFilter){
        return companyFilter.toSpecification(licenceSpecificationsFactory);
    }
}

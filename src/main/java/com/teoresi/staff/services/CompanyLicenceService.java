package com.teoresi.staff.services;

import com.teoresi.staff.components.CompanyLicenceSpecificationsFactory;
import com.teoresi.staff.entities.CompanyLicence;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.repositories.CompanyLicenceRepository;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.shared.services.BasicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyLicenceService extends BasicService {

    private final CompanyLicenceRepository companyLicenceRepository;
    private final CompanyLicenceSpecificationsFactory companyLicenceSpecificationsFactory;

    private final LicenceExpiryEmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(CompanyLicenceService.class);
    private static final String COMPANY_ID_NOT_FOUND = "Company with id %d not found.";

    public CompanyLicenceService(SessionService sessionService, CompanyLicenceRepository companyLicenceRepository, CompanyLicenceSpecificationsFactory companyLicenceSpecificationsFactory, LicenceExpiryEmailService emailService) {
        super(sessionService, LoggerFactory.getLogger(CompanyLicenceService.class));
        this.companyLicenceRepository = companyLicenceRepository;
        this.companyLicenceSpecificationsFactory = companyLicenceSpecificationsFactory;
        this.emailService = emailService;
    }

    public CompanyLicence create(CompanyLicence companyLicence) {
        companyLicence.setId(null);
        return save(companyLicenceRepository, companyLicence);
    }

    public CompanyLicence update(CompanyLicence companyLicence) {
        if (!companyLicenceRepository.existsById(companyLicence.getId())) {
            throw buildEntityWithIdNotFoundException(companyLicence.getId(), COMPANY_ID_NOT_FOUND);
        }
        return save(companyLicenceRepository, companyLicence);
    }

    public CompanyLicence getById(Long id) {
        return getById(companyLicenceRepository, id, COMPANY_ID_NOT_FOUND);
    }

    public void deleteById(Long id) {
        deleteById(companyLicenceRepository, id, COMPANY_ID_NOT_FOUND);
    }

    public List<CompanyLicence> getAll(){
        return getAll(companyLicenceRepository);
    }


    public Page<CompanyLicence> searchAdvanced(Optional<Filter<CompanyLicence>> filter, Pageable pageable) {
        try {
            return filter.map(companyLicenceFilter ->
                    companyLicenceRepository.findAll(getSpecificationForAdvancedSearch(companyLicenceFilter), pageable)
            ).orElseGet(() -> companyLicenceRepository.findAll(pageable));
        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Specification<CompanyLicence> getSpecificationForAdvancedSearch(Filter<CompanyLicence> companyLicenceFilter){
        return companyLicenceFilter.toSpecification(companyLicenceSpecificationsFactory);
    }

    public CompanyLicence sendEmailById(Long id) {
        CompanyLicence companyLicence = getById(id);
        emailService.notifyCompanyAboutLicence(companyLicence);
        return companyLicence;
    }

    public void notifyAboutExpiringLicence() {
        List<CompanyLicence> companyLicences = getAll();
        LocalDate currentDate = getLocalDate(new Date());

        for (CompanyLicence companyLicence : companyLicences){
            LocalDate expiryDate = getLocalDate(companyLicence.getExpiryDate()).minusMonths(1);

            if(expiryDate.isEqual(currentDate) || expiryDate.isAfter(currentDate))
                if(!companyLicence.isEmailSent()) {
                    emailService.notifyCompanyAboutLicence(companyLicence);
                    companyLicence.setEmailSent(true);
                }
        }
    }
}

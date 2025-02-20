package com.client.staff.services;

import com.client.staff.components.CompanySpecificationsFactory;
import com.client.staff.entities.Company;
import com.client.staff.libs.data.models.Filter;
import com.client.staff.repositories.CompanyRepository;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService extends BasicService {

    private final CompanyRepository companyRepository;
    private final CompanySpecificationsFactory companySpecificationsFactory;
    private final Logger logger = LoggerFactory.getLogger(CompanyService.class);
    private static final String COMPANY_LICENCE_ID_NOT_FOUND = "Punto vendita con id %d non trovato";
    private static final String COMPANY_NAME_UNIQUE_ERROR = "Esiste un punto vendita con lo stesso nome";

    public CompanyService(SessionService sessionService, CompanyRepository companyRepository, CompanySpecificationsFactory companySpecificationsFactory) {
        super(sessionService, LoggerFactory.getLogger(CompanyService.class));
        this.companyRepository = companyRepository;
        this.companySpecificationsFactory = companySpecificationsFactory;
    }

    public Company create(Company company) {
        company.setId(null);

        try {
            String documentDirectory = Company.computeDirectory(company);
            Path path = Paths.get(ARCHIVE_DIRECTORY + documentDirectory);
            Files.createDirectories(path);
            company.setDirectory(documentDirectory);
            return save(companyRepository, company);

        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public Company update(Company company) {
        Company storedCompany = getById(company.getId());
        company.setDirectory(storedCompany.getDirectory());
        return save(companyRepository, company);
    }

    @Override
    protected String handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = CONSTRAINT_VIOLATION;

        if (ex.getCause() != null) {
            String detailedMessage = ex.getCause().getCause().getMessage();
            if (detailedMessage.contains("company_name_unique")) {
                message = COMPANY_NAME_UNIQUE_ERROR;
            }
        }
        return message;
    }

    public Company getById(Long id) {
        return getById(companyRepository, id, COMPANY_LICENCE_ID_NOT_FOUND);
    }

    public void deleteById(Long id) {
        deleteById(companyRepository, id, COMPANY_LICENCE_ID_NOT_FOUND);
    }

    public List<Company> getAll(){
        return getAll(companyRepository);
    }


    public Page<Company> searchAdvanced(Optional<Filter<Company>> filter, Pageable pageable) {
        try {
            return filter.map(companyFilter ->
                    companyRepository.findAll(getSpecificationForAdvancedSearch(companyFilter), pageable)
            ).orElseGet(() -> companyRepository.findAll(pageable));
        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Specification<Company> getSpecificationForAdvancedSearch(Filter<Company> companyFilter){
        return companyFilter.toSpecification(companySpecificationsFactory);
    }
}

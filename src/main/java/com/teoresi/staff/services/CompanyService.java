package com.teoresi.staff.services;

import com.teoresi.staff.components.CompanySpecificationsFactory;
import com.teoresi.staff.entities.Company;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.repositories.CompanyRepository;
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
    private static final String COMPANY_LICENCE_ID_NOT_FOUND = "Unit with id %d not found.";

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
        if (!companyRepository.existsById(company.getId())) {
            throw buildEntityWithIdNotFoundException(company.getId(), COMPANY_LICENCE_ID_NOT_FOUND);
        }
        return save(companyRepository, company);
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

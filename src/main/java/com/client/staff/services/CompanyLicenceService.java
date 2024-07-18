package com.client.staff.services;

import com.client.staff.components.CompanyLicenceSpecificationsFactory;
import com.client.staff.entities.Company;
import com.client.staff.entities.CompanyLicence;
import com.client.staff.entities.Licence;
import com.client.staff.libs.data.models.Filter;
import com.client.staff.libs.utils.ZipUtil;
import com.client.staff.repositories.CompanyLicenceRepository;
import com.client.staff.security.services.SessionService;
import com.client.staff.shared.services.BasicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyLicenceService extends BasicService {

    private final CompanyLicenceRepository companyLicenceRepository;
    private final CompanyService companyService;
    private final LicenceService licenceService;
    private final CompanyLicenceSpecificationsFactory companyLicenceSpecificationsFactory;

    private final LicenceExpiryEmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(CompanyLicenceService.class);
    private static final String COMPANY_LICENCE_ID_NOT_FOUND = "Il contratto con id %d non esiste";

    public CompanyLicenceService(SessionService sessionService, CompanyLicenceRepository companyLicenceRepository, CompanyService companyService, LicenceService licenceService, CompanyLicenceSpecificationsFactory companyLicenceSpecificationsFactory, LicenceExpiryEmailService emailService) {
        super(sessionService, LoggerFactory.getLogger(CompanyLicenceService.class));
        this.companyLicenceRepository = companyLicenceRepository;
        this.companyService = companyService;
        this.licenceService = licenceService;
        this.companyLicenceSpecificationsFactory = companyLicenceSpecificationsFactory;
        this.emailService = emailService;
    }

    public CompanyLicence create(CompanyLicence companyLicence) {
        companyLicence.setId(null);
        Company company = companyService.getById(companyLicence.getCompany().getId());
        Licence licence = licenceService.getById(companyLicence.getLicence().getId());
        String directory = CompanyLicence.computeDirectory(company, licence);

        try {
            Path path = Paths.get(ARCHIVE_DIRECTORY + directory);
            Files.createDirectories(path);

            companyLicence.setDirectory(directory);
            return save(companyLicenceRepository, companyLicence);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }


    public void uploadDocument(Long id, MultipartFile file) throws IOException {

        CompanyLicence companyLicence = getById(companyLicenceRepository, id, COMPANY_LICENCE_ID_NOT_FOUND);
        String directory = companyLicence.getDirectory();

        byte[] bytes = file.getBytes();
        String filePath = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        Path path = Paths.get(ARCHIVE_DIRECTORY + directory + addTimestampToFilename(filePath));
        Files.write(path, bytes);
    }


    private static String addTimestampToFilename(String originalFilename) {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedTimestamp = timestamp.format(formatter);

        int lastIndex = originalFilename.lastIndexOf('.');
        if (lastIndex == -1) {
            return originalFilename + "_" + formattedTimestamp;
        } else {
            String name = originalFilename.substring(0, lastIndex);
            String extension = originalFilename.substring(lastIndex);
            return name + "-" + formattedTimestamp + extension;
        }
    }


    public FileSystemResource downloadDocuments(Long id) throws IOException{

        CompanyLicence companyLicence = getById(companyLicenceRepository, id, COMPANY_LICENCE_ID_NOT_FOUND);
        String directory = companyLicence.getCompany().getDirectory();
        String documentDirectory = directory + companyLicence.getLicence().getDirectory();

        String zipPath = ZipUtil.zipFolder(ARCHIVE_DIRECTORY + documentDirectory);

        return new FileSystemResource(zipPath);
    }


    public CompanyLicence getById(Long id){
        return getById(companyLicenceRepository, id, COMPANY_LICENCE_ID_NOT_FOUND);
    }

    public CompanyLicence update(CompanyLicence companyLicence) {
        if (!companyLicenceRepository.existsById(companyLicence.getId())) {
            throw buildEntityWithIdNotFoundException(companyLicence.getId(), COMPANY_LICENCE_ID_NOT_FOUND);
        }
        return save(companyLicenceRepository, companyLicence);
    }


    public void deleteById(Long id) {
        deleteById(companyLicenceRepository, id, COMPANY_LICENCE_ID_NOT_FOUND);
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
        CompanyLicence companyLicence = getById(companyLicenceRepository,id, COMPANY_LICENCE_ID_NOT_FOUND);
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

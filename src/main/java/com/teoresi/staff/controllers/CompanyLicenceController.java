package com.teoresi.staff.controllers;

import com.teoresi.staff.dtos.CompanyLicenceDTO;
import com.teoresi.staff.entities.CompanyLicence;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.mappers.CompanyLicenceMapper;
import com.teoresi.staff.services.CompanyLicenceService;
import com.teoresi.staff.services.old.UnitService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Sinks;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("company-licences")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class CompanyLicenceController {

    private final CompanyLicenceService companyLicenceService;
    private final CompanyLicenceMapper companyLicenceMapper;

    private final Logger logger = LoggerFactory.getLogger(UnitService.class);


    // ------------------------------  REQUIREMENTS SATISFIED ------------------------------ //

    @PostMapping
    public CompanyLicenceDTO create(@Valid @RequestBody CompanyLicenceDTO companyLicenceDTO) throws IOException, ParseException, InterruptedException {
        CompanyLicence companyLicence = companyLicenceMapper.convertDtoToModel(companyLicenceDTO);
        return companyLicenceMapper.convertModelToDTO(companyLicenceService.create(companyLicence));
    }

    @PutMapping("/{id}")
    public CompanyLicenceDTO update(@PathVariable Long id, @Valid @RequestBody CompanyLicenceDTO companyLicenceDTO) {
        companyLicenceDTO.setId(id);
        CompanyLicence companyLicence = companyLicenceMapper.convertDtoToModel(companyLicenceDTO);
        return companyLicenceMapper.convertModelToDTO(companyLicenceService.update(companyLicence));
    }

    @PostMapping("/advanced-search")
    public PageDTO<CompanyLicenceDTO> searchAdvanced(
            @RequestBody(required = false) Optional<Filter<CompanyLicence>> filter,
            @PageableDefault Pageable pageable) {

        Page<CompanyLicence> resultPage = companyLicenceService.searchAdvanced(filter, pageable);
        return companyLicenceMapper.convertModelsPageToDtosPage(resultPage);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        companyLicenceService.deleteById(id);
    }

    @GetMapping("/{id}")
    public CompanyLicenceDTO getById(@PathVariable Long id) {
        return companyLicenceMapper.convertModelToDTO(companyLicenceService.getById(id));
    }

    @GetMapping("/")
    public List<CompanyLicenceDTO> getAll() {
        return companyLicenceMapper.convertModelsToDtos(companyLicenceService.getAll());
    }

    @PostMapping("/{id}/send-email")
    public CompanyLicenceDTO sendEmail(@PathVariable Long id) {
        return companyLicenceMapper.convertModelToDTO(companyLicenceService.sendEmailById(id));
    }


    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try {
            companyLicenceService.uploadDocument(id, file);
            return ResponseEntity.ok("File uploaded successfully");

        } catch (IOException e) {
            logger.error("Error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }


    @GetMapping("/{id}/download")
    public ResponseEntity<FileSystemResource> downloadFiles(@PathVariable Long id) {
        try {
            FileSystemResource resource = companyLicenceService.downloadDocuments(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(resource.contentLength());
            headers.setContentDispositionFormData("attachment", resource.getFilename());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            logger.error("Error during files download: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}

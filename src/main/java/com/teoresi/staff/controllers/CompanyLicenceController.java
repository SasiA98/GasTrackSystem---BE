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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("company-licences")
public class CompanyLicenceController {

    private final CompanyLicenceService companyLicenceService;
    private final CompanyLicenceMapper companyLicenceMapper;

    private final Logger logger = LoggerFactory.getLogger(UnitService.class);


    // ------------------------------  REQUIREMENTS SATISFIED ------------------------------ //

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CompanyLicenceDTO create(@Valid @RequestBody CompanyLicenceDTO companyLicenceDTO) throws IOException, ParseException, InterruptedException {
        CompanyLicence companyLicence = companyLicenceMapper.convertDtoToModel(companyLicenceDTO);
        return companyLicenceMapper.convertModelToDTO(companyLicenceService.create(companyLicence));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public CompanyLicenceDTO update(@PathVariable Long id, @Valid @RequestBody CompanyLicenceDTO companyLicenceDTO) {
        companyLicenceDTO.setId(id);
        CompanyLicence companyLicence = companyLicenceMapper.convertDtoToModel(companyLicenceDTO);
        return companyLicenceMapper.convertModelToDTO(companyLicenceService.update(companyLicence));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/advanced-search")
    public PageDTO<CompanyLicenceDTO> searchAdvanced(
            @RequestBody(required = false) Optional<Filter<CompanyLicence>> filter,
            @PageableDefault Pageable pageable) {

        Page<CompanyLicence> resultPage = companyLicenceService.searchAdvanced(filter, pageable);
        return companyLicenceMapper.convertModelsPageToDtosPage(resultPage);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN',)")
    public void deleteById(@PathVariable Long id) {
        companyLicenceService.deleteById(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CompanyLicenceDTO getById(@PathVariable Long id) {
        return companyLicenceMapper.convertModelToDTO(companyLicenceService.getById(id));
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<CompanyLicenceDTO> getAll() {
        return companyLicenceMapper.convertModelsToDtos(companyLicenceService.getAll());
    }

}

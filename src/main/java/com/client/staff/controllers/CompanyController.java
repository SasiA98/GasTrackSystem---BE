package com.client.staff.controllers;

import com.client.staff.dtos.CompanyDTO;
import com.client.staff.dtos.CompanyLicenceDTO;
import com.client.staff.entities.Company;
import com.client.staff.libs.data.models.Filter;
import com.client.staff.libs.web.dtos.PageDTO;
import com.client.staff.mappers.CompanyLicenceMapper;
import com.client.staff.mappers.CompanyMapper;
import com.client.staff.services.CompanyLicenceService;
import com.client.staff.services.CompanyService;
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
@RequestMapping("companies")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyLicenceService companyLicenceService;
    private final CompanyMapper companyMapper;
    private final CompanyLicenceMapper companyLicenceMapper;
    private final Logger logger = LoggerFactory.getLogger(CompanyService.class);


    // ------------------------------  REQUIREMENTS SATISFIED ------------------------------ //

    @PostMapping
    public CompanyDTO create(@Valid @RequestBody CompanyDTO companyDTO) throws IOException, ParseException, InterruptedException {
        Company company = companyMapper.convertDtoToModel(companyDTO);
        return companyMapper.convertModelToDTO(companyService.create(company));
    }

    @PutMapping("/{id}")
    public CompanyDTO update(@PathVariable Long id, @Valid @RequestBody CompanyDTO companyDTO) {
        companyDTO.setId(id);
        Company company = companyMapper.convertDtoToModel(companyDTO);
        return companyMapper.convertModelToDTO(companyService.update(company));
    }

    @GetMapping("/")
    public List<CompanyDTO> getAll() {
        return companyMapper.convertModelsToDtos(companyService.getAll());
    }

    @GetMapping("/{id}/company-licences")
    public List<CompanyLicenceDTO> getAllCompanyLicencesById(@PathVariable Long id) {
        return companyLicenceMapper.convertModelsToDtos(companyLicenceService.getAllByCompanyId(id));
    }


    @GetMapping("/{id}")
    public CompanyDTO getById(@PathVariable Long id) {
        return companyMapper.convertModelToDTO(companyService.getById(id));
    }

    @PostMapping("/advanced-search")
    public PageDTO<CompanyDTO> searchAdvanced(
            @RequestBody(required = false) Optional<Filter<Company>> filter,
            @PageableDefault Pageable pageable) {

        Page<Company> resultPage = companyService.searchAdvanced(filter, pageable);
        return companyMapper.convertModelsPageToDtosPage(resultPage);
    }

}

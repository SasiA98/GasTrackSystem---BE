package com.teoresi.staff.controllers;

import com.teoresi.staff.dtos.LicenceDTO;
import com.teoresi.staff.entities.Licence;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.mappers.LicenceMapper;
import com.teoresi.staff.services.CompanyService;
import com.teoresi.staff.services.LicenceService;
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
@RequestMapping("licences")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class LicenceController {

    private final LicenceService licenceService;
    private final LicenceMapper licenceMapper;
    private final Logger logger = LoggerFactory.getLogger(CompanyService.class);


    // ------------------------------  REQUIREMENTS SATISFIED ------------------------------ //

    @PostMapping
    public LicenceDTO create(@Valid @RequestBody LicenceDTO licenceDTO) throws IOException, ParseException, InterruptedException {
        Licence licence = licenceMapper.convertDtoToModel(licenceDTO);
        return licenceMapper.convertModelToDTO(licenceService.create(licence));
    }

    @PutMapping("/{id}")
    public LicenceDTO update(@PathVariable Long id, @Valid @RequestBody LicenceDTO licenceDTO) {
        licenceDTO.setId(id);
        Licence licence = licenceMapper.convertDtoToModel(licenceDTO);
        return licenceMapper.convertModelToDTO(licenceService.update(licence));
    }

    @GetMapping("/")
    public List<LicenceDTO> getAll() {
        return licenceMapper.convertModelsToDtos(licenceService.getAll());
    }

    @GetMapping("/{id}")
    public LicenceDTO getById(@PathVariable Long id) {
        return licenceMapper.convertModelToDTO(licenceService.getById(id));
    }

    @PostMapping("/advanced-search")
    public PageDTO<LicenceDTO> searchAdvanced(
            @RequestBody(required = false) Optional<Filter<Licence>> filter,
            @PageableDefault Pageable pageable) {

        Page<Licence> resultPage = licenceService.searchAdvanced(filter, pageable);
        return licenceMapper.convertModelsPageToDtosPage(resultPage);
    }

}

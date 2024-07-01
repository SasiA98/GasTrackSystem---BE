package com.teoresi.staff.controllers.old;

import com.teoresi.staff.dtos.old.SkillDTO;
import com.teoresi.staff.entities.old.Skill;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.mappers.old.SkillMapper;
import com.teoresi.staff.services.old.SkillService;
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
@RequestMapping("skills")
public class SkillController {

    private final SkillService skillService;
    private final SkillMapper skillMapper;

    private final Logger logger = LoggerFactory.getLogger(UnitService.class);


    // ------------------------------  REQUIREMENTS SATISFIED ------------------------------ //

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'STAFF')")
    public SkillDTO create(@Valid @RequestBody SkillDTO skillDTO) throws IOException, ParseException, InterruptedException {
        Skill skill = skillMapper.convertDtoToModel(skillDTO);
        return skillMapper.convertModelToDTO(skillService.create(skill));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'STAFF')")
    public SkillDTO update(@PathVariable Long id, @Valid @RequestBody SkillDTO skillDTO) {
        skillDTO.setId(id);
        Skill skill = skillMapper.convertDtoToModel(skillDTO);
        return skillMapper.convertModelToDTO(skillService.update(skill));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PM', 'PSL', 'PSE', 'PSM', 'STAFF')")
    public SkillDTO getById(@PathVariable Long id) {
        return skillMapper.convertModelToDTO(skillService.getById(id));
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PM', 'PSL', 'PSE', 'PSM', 'STAFF')")
    public List<SkillDTO> getAll() {
        return skillMapper.convertModelsToDtos(skillService.getAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'STAFF')")
    public void deleteById(@PathVariable Long id) {
        skillService.deleteById(id);
    }

    @PostMapping("/advanced-search")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PM', 'PSL', 'PSE', 'PSM', 'STAFF')")
    public PageDTO<SkillDTO> searchAdvanced(
            @RequestBody(required = false) Optional<Filter<Skill>> filter,
            @PageableDefault Pageable pageable) {

        Page<Skill> resultPage = skillService.searchAdvanced(filter, pageable);
        return skillMapper.convertModelsPageToDtosPage(resultPage);
    }

}

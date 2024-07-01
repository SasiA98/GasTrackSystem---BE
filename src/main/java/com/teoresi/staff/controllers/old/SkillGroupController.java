package com.teoresi.staff.controllers.old;

import com.teoresi.staff.dtos.old.SkillGroupDTO;
import com.teoresi.staff.entities.old.SkillGroup;
import com.teoresi.staff.mappers.old.SkillGroupMapper;
import com.teoresi.staff.services.old.SkillGroupService;
import com.teoresi.staff.services.old.UnitService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("skillsGroups")
public class SkillGroupController {

    private final SkillGroupService skillGroupService;
    private final SkillGroupMapper skillGroupMapper;

    private final Logger logger = LoggerFactory.getLogger(UnitService.class);


    // ------------------------------  REQUIREMENTS SATISFIED ------------------------------ //

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'STAFF')")
    public SkillGroupDTO create(@Valid @RequestBody SkillGroupDTO skillGroupDTO) throws IOException, ParseException, InterruptedException {
        SkillGroup skillGroup = skillGroupMapper.convertDtoToModel(skillGroupDTO);
        return skillGroupMapper.convertModelToDTO(skillGroupService.create(skillGroup));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'STAFF')")
    public SkillGroupDTO update(@PathVariable Long id, @Valid @RequestBody SkillGroupDTO skillGroupDTO) {
        skillGroupDTO.setId(id);
        SkillGroup skillGroup = skillGroupMapper.convertDtoToModel(skillGroupDTO);
        return skillGroupMapper.convertModelToDTO(skillGroupService.update(skillGroup));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PM', 'PSL', 'PSE', 'PSM', 'STAFF')")
    public SkillGroupDTO getById(@PathVariable Long id) {
        return skillGroupMapper.convertModelToDTO(skillGroupService.getById(id));
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PM', 'PSL', 'PSE', 'PSM', 'STAFF')")
    public List<SkillGroupDTO> getAll() {
        return skillGroupMapper.convertModelsToDtos(skillGroupService.getAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'STAFF')")
    public void deleteById(@PathVariable Long id) {
        skillGroupService.deleteById(id);
    }

}

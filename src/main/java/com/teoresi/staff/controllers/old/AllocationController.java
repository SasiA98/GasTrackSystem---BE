package com.teoresi.staff.controllers.old;

import com.teoresi.staff.dtos.old.AllocationDTO;
import com.teoresi.staff.entities.old.Allocation;
import com.teoresi.staff.mappers.old.AllocationMapper;
import com.teoresi.staff.services.old.AllocationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;

@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("allocations")
public class AllocationController {

    private final AllocationService allocationService;
    private final AllocationMapper allocationMapper;
    private final Logger logger = LoggerFactory.getLogger(AllocationService.class);
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL')")
    public AllocationDTO create(@Valid @RequestBody AllocationDTO allocationDTO) throws IOException, ParseException, InterruptedException {
        Allocation allocation = allocationMapper.convertDtoToModel(allocationDTO);
        return allocationMapper.convertModelToDTO(allocationService.create(allocation));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL')")
    public AllocationDTO update(@PathVariable Long id, @Valid @RequestBody AllocationDTO allocationDTO) {
        allocationDTO.setId(id);
        Allocation allocation = allocationMapper.convertDtoToModel(allocationDTO);
        return allocationMapper.convertModelToDTO(allocationService.update(allocation));
    }

    @PutMapping("/{id}/convert")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL')")
    public AllocationDTO convertById(@PathVariable Long id, boolean fromRealToSale) {
        return allocationMapper.convertModelToDTO(allocationService.convertById(id, fromRealToSale));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL')")
    public void deleteById(@PathVariable Long id) {
        allocationService.delete(id);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL')")
    public AllocationDTO getById(@PathVariable Long id) {
        return allocationMapper.convertModelToDTO(allocationService.getById(id));
    }
}

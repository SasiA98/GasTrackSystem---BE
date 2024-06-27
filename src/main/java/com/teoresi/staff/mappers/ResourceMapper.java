package com.teoresi.staff.mappers;

import com.teoresi.staff.dtos.UnitDTO;
import com.teoresi.staff.entities.Unit;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.dtos.ResourceDTO;
import com.teoresi.staff.entities.Resource;

import com.teoresi.staff.services.ResourceService;
import com.teoresi.staff.shared.components.SpringContext;
import com.teoresi.staff.shared.models.Role;
import org.mapstruct.*;
import org.springframework.data.domain.Page;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface ResourceMapper {

    Resource convertDtoToModel(ResourceDTO resourceDTO);

    default ResourceDTO convertModelToDTO(Resource resource){
        if ( resource == null ) {
            return null;
        }

        ResourceDTO resourceDTO = new ResourceDTO();

        if ( resource.getId() != null ) {
            resourceDTO.setId( resource.getId() );
        }
        resourceDTO.setEmployeeId( resource.getEmployeeId() );
        resourceDTO.setEmail( resource.getEmail() );
        resourceDTO.setName( resource.getName() );
        resourceDTO.setSurname( resource.getSurname() );
        resourceDTO.setBirthDate( resource.getBirthDate() );
        resourceDTO.setHiringDate( resource.getHiringDate() );
        resourceDTO.setLeaveDate( resource.getLeaveDate() );
        resourceDTO.setSite( resource.getSite() );
        resourceDTO.setUnit( unitToUnitDTO( resource.getUnit() ) );
        resourceDTO.setLocation( resource.getLocation() );

        resourceDTO.setLastHourlyCost( resource.getLastHourlyCost() );
        resourceDTO.setCurrentHourlyCost( resource.getCurrentHourlyCost() );
        resourceDTO.setLastHourlyCostStartDate( resource.getLastHourlyCostStartDate() );

        resourceDTO.setLastWorkingTime( resource.getLastWorkingTime() );
        resourceDTO.setLastWorkingTimeStartDate( resource.getLastWorkingTimeStartDate() );

        Set<Role> set = resource.getRoles();
        if ( set != null ) {
            resourceDTO.setRoles( new HashSet<>( set ) );
        }
        resourceDTO.setTrigram( resource.getTrigram() );
        resourceDTO.setRal( resource.getRal() );
        resourceDTO.setRalStartDate( resource.getRalStartDate() );
        resourceDTO.setDailyAllowance( resource.getDailyAllowance() );
        resourceDTO.setDailyAllowanceStartDate( resource.getDailyAllowanceStartDate() );
        resourceDTO.setCcnlLevel( resource.getCcnlLevel() );
        resourceDTO.setCcnlLevelStartDate( resource.getCcnlLevelStartDate() );
        resourceDTO.setNote( resource.getNote() );


        ResourceService resourceService = SpringContext.getBean(ResourceService.class);
        if(!resourceService.currentUserHasSalaryDetailPermissions())
            setHourlyCostVisibilityFilter(resourceDTO);
        return resourceDTO;
    }

    List<ResourceDTO> convertModelsToDtos(List<Resource> resources);

    default PageDTO<ResourceDTO> convertModelsPageToDtosPage(Page<Resource> modelsPage) {
        return PageDTO.<ResourceDTO>builder()
                .content(convertModelsToDtos(modelsPage.getContent()))
                .first(modelsPage.isFirst())
                .last(modelsPage.isLast())
                .number(modelsPage.getNumber())
                .numberOfElements(modelsPage.getNumberOfElements())
                .size(modelsPage.getSize())
                .sort(modelsPage.getSort().toList())
                .totalElements(modelsPage.getTotalElements())
                .totalPages(modelsPage.getTotalPages())
                .build();
    }

    default void setHourlyCostVisibilityFilter(ResourceDTO resourceDTO){
        resourceDTO.setLastHourlyCost(null);
        resourceDTO.setLastHourlyCostStartDate(null);
        resourceDTO.setCurrentHourlyCost(null);
        resourceDTO.setLastWorkingTimeStartDate(null);
        resourceDTO.setLastWorkingTime(0);
        resourceDTO.setRal(null);
        resourceDTO.setRalStartDate(null);
        resourceDTO.setDailyAllowance(null);
        resourceDTO.setDailyAllowanceStartDate(null);
        resourceDTO.setCcnlLevel(null);
        resourceDTO.setCcnlLevelStartDate(null);
    }

    default UnitDTO unitToUnitDTO(Unit unit) {
        if ( unit == null ) {
            return null;
        }

        UnitDTO unitDTO = new UnitDTO();

        if ( unit.getId() != null ) {
            unitDTO.setId( unit.getId() );
        }
        unitDTO.setTrigram( unit.getTrigram() );
        unitDTO.setType( unit.getType() );
        unitDTO.setStatus( unit.getStatus() );

        return unitDTO;
    }
}

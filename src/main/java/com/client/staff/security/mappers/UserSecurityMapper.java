package com.client.staff.security.mappers;

import com.client.staff.security.models.UserSecurityDetails;
import com.client.staff.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel="spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSecurityMapper {

    @Mapping(target = "username", source = "resource.email")
    @Mapping(target = "authorities", source = "resource.roles")
    @Mapping(target = "isEnabled", expression = "java(true)")
    @Mapping(target = "isCredentialsNonExpired", expression = "java(true)")
    @Mapping(target = "isAccountNonLocked", expression = "java(true)")
    @Mapping(target = "isAccountNonExpired", expression = "java(true)")
    UserSecurityDetails mapToUserSecurityDetails(User user);

}

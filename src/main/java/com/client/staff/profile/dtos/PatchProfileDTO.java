package com.client.staff.profile.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.client.staff.dtos.ResourceDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatchProfileDTO {

    private ResourceDTO resource;

    private String status;

    private String password;

    private String newPassword;

}

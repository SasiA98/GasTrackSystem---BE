package com.teoresi.staff.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestDTO {

    private String username;

    private String password;

    private String roles;

}

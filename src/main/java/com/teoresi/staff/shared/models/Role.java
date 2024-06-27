package com.teoresi.staff.shared.models;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ADMIN,
    CONSULTANT,
    DTL,
    DUM,
    GDM,
    PM,
    PSE,
    PSL,
    STAFF,
    PSM;


    public String getAuthority() {
        return this.name();
    }

}

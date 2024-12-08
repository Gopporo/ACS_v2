package org.example.acs_v2.models.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role  implements GrantedAuthority {
    ROLE_USER, ROLE_DIRECTOR, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}

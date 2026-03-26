package org.example.acs_v2.models;

import org.springframework.security.core.GrantedAuthority;

public enum Role  implements GrantedAuthority {
    ROLE_USER, ROLE_DIRECTOR, ROLE_ADMIN, ROLE_SECURITY;

    @Override
    public String getAuthority() {
        return name();
    }
}

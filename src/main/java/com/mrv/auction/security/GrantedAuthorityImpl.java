package com.mrv.auction.security;

import org.springframework.security.core.GrantedAuthority;

public class GrantedAuthorityImpl implements GrantedAuthority {

    private final String authority;
    private final boolean role;

    public GrantedAuthorityImpl(String authority, boolean role){
        this.authority = authority;
        this.role = role;
    }

    @Override
    public String getAuthority() {
        if (role){
            return "ROLE_" + authority;
        }
        return authority;
    }
}

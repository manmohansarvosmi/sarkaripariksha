package com.helixion.sarkariPariksha.Security;

import com.helixion.sarkariPariksha.Entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String mobileNumber;
    private final Long candidateId;
    private final String fullName;
    private final String role;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Users user, Long candidateId, String fullName) {
        this.userId = user.getId();
        this.mobileNumber = user.getMobileNumber();
        this.candidateId = candidateId;
        this.fullName = fullName;
        this.role = (user.getRole() != null) ? user.getRole() : "ROLE_CANDIDATE";
        this.active = (user.getActive() != null) ? user.getActive() : true;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return ""; // Mobile OTP based authentication
    }

    @Override
    public String getUsername() {
        return mobileNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}


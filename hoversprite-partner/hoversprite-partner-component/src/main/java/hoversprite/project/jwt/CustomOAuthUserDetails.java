package hoversprite.project.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomOAuthUserDetails implements UserDetails {
    private final String username;
    private final Long userId;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuthUserDetails(String username, Long userId, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.userId = userId;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // OAuth 2.0 doesn't use password
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Or implement your own logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Or implement your own logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Or implement your own logic
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
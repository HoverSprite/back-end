package hoversprite.project.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final String username;
    private final Long userId;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String password;
    private final String provider;
    private final boolean enabled;

    public CustomUserDetails(String username, String password, Long userId, Collection<? extends GrantedAuthority> authorities, String provider, boolean enabled) {
        this.username = username;
        this.password = password;
        this.userId = userId;
        this.authorities = authorities;
        this.provider = provider;
        this.enabled = enabled;
    }

    public String getProvider() {
        return provider;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password; // Return the actual password for non-OAuth authentication
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
        return enabled;
    }
}
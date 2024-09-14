package hoversprite.project.util;

import hoversprite.project.common.domain.PersonRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static PersonRole getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        return PersonRole.valueOf(authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object details = authentication.getDetails();
        if (details instanceof Long) {
            return (Long) details;
        }

        throw new IllegalStateException("User ID not found in Authentication details");
    }
}
package hoversprite.project.jwt;

import hoversprite.project.common.domain.PersonAuthor;
import hoversprite.project.partner.PersonDTO;
import hoversprite.project.partner.PersonGlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonGlobalService personGlobalService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PersonDTO person = personGlobalService.findByEmailAddress(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        UserDetails userDetails = User.builder()
                .username(person.getEmailAddress())
                .password(person.getPasswordHash())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + person.getRole().name())))
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        auth.setDetails(person.getId());

        SecurityContextHolder.getContext().setAuthentication(auth);

        return userDetails;
    }
}

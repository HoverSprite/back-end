package hoversprite.project.jwt;

import hoversprite.project.partner.PersonDTO;
import hoversprite.project.partner.PersonGlobalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private PersonGlobalService personGlobalService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        logger.debug("Loaded OAuth2User: {}", user);
        return user;
    }

    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PersonDTO person = personGlobalService.findByEmailAddress(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new CustomUserDetails(
                person.getEmailAddress(),
                null,
                person.getId(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + person.getRole().name())),
                person.getOauthProvider(),
                true
        );
    }
}
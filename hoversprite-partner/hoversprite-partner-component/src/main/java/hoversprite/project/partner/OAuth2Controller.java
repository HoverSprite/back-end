package hoversprite.project.partner;

import hoversprite.project.jwt.*;
import hoversprite.project.request.PersonRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private PersonService personService;

    private final CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2Controller(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @GetMapping("/callback")
    public RedirectView oauth2Callback(@RequestParam("email") String email,
                                       @RequestParam("name") String name,
                                       @RequestParam("authType") String authType,
                                       @RequestParam("provider") String provider,
                                       HttpServletResponse response) throws IOException {
        if (email == null || name == null) {
            return new RedirectView("http://localhost:3000/signin?error=invalid_user_info");
        }

        if ("signup".equals(authType)) {
            return handleSignup(email, name, provider);
        } else if ("signin".equals(authType)) {
            return handleSignin(email, response, provider);
        } else {
            return new RedirectView("http://localhost:3000/signin?error=invalid_auth_type");
        }
    }

    private RedirectView handleSignup(String email, String name, String provider) {
        if (personService.existsByEmail(email)) {
            return new RedirectView("http://localhost:3000/signup?error=email_exists");
        }

        PersonRequest personRequest = new PersonRequest();
        personRequest.setEmailAddress(email);
        personRequest.setFullName(name);
        personRequest.setOauthProvider(provider);

        String token = jwtUtil.generateTokenWithUserInfo(personRequest.getEmailAddress(), personRequest.getFullName(),
                "picture", "temporary", provider);
        return new RedirectView("http://localhost:3000/role-selection?token=" + token);
    }

    private RedirectView handleSignin(String email, HttpServletResponse response, String provider) {
        if (personService.existsByEmail(email)) {
            // User exists, but we need to check if they were created via OAuth2 or normal signup
            PersonDTO person = personService.findByEmailAddress(email).orElseThrow(() -> new RuntimeException("No account found by this email."));
            if (person.getOauthProvider().equals(provider)) {
                final CustomUserDetails userDetails = customOAuth2UserService.loadUserByUsername(email);
                final String accessToken = jwtUtil.generateToken(userDetails, person.getOauthProvider());
                final String refreshToken = jwtUtil.generateRefreshToken(userDetails, person.getOauthProvider());

                addRefreshTokenCookie(response, refreshToken);

                return new RedirectView("http://localhost:3000/dashboard?token=" + accessToken);
            } else {
                // Account is linked to a different OAuth2 provider
                logger.warn("Account is already linked to a different OAuth provider: {} for email: {}",
                        person.getOauthProvider(), email);
                return new RedirectView("http://localhost:3000/signin?error=account_linked_to_different_provider" +
                        "&provider=" + person.getOauthProvider());
            }
        } else {
            return new RedirectView("http://localhost:3000/signin?error=user_not_found");
        }
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(604800) // 7 days
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
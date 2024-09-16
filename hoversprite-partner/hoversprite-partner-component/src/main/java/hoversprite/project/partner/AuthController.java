package hoversprite.project.partner;


import hoversprite.project.jwt.*;
import hoversprite.project.request.PersonRequest;
import hoversprite.project.response.EmailValidationResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${jwt.refresh-expiration}")
    private int refreshTokenDuration;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final PersonService personService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          CustomUserDetailsService userDetailsService, PersonService personService,
                          CustomOAuth2UserService customOAuth2UserService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.personService = personService;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody PersonRequest personRequest) {

        if (personService.existsByEmail(personRequest.getEmailAddress())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        personRequest.setPasswordHash(passwordEncoder.encode(personRequest.getPasswordHash()));
        PersonDTO newPerson = personService.addPerson(personRequest);

        final CustomUserDetails userDetails = userDetailsService.loadUserByUsername(newPerson.getEmailAddress());

        final String accessToken = jwtUtil.generateToken(userDetails, newPerson.getOauthProvider());

        return ResponseEntity.ok()
                .body(new AuthenticationResponse(accessToken));
    }

    @PostMapping("/oauth2-signup")
    public ResponseEntity<?> oauth2Signup(@RequestBody PersonRequest personRequest) {

        if (personService.existsByEmail(personRequest.getEmailAddress())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }
        personRequest.setPasswordHash(null);
        PersonDTO newPerson = personService.addPerson(personRequest);

        final CustomUserDetails userDetails = customOAuth2UserService.loadUserByUsername(newPerson.getEmailAddress());

        final String accessToken = jwtUtil.generateToken(userDetails, newPerson.getOauthProvider());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails, newPerson.getOauthProvider());

        // Set refresh token as HttpOnly cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenDuration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthenticationResponse(accessToken));
    }

    @GetMapping("/email")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        boolean isEmailTaken = personService.existsByEmail(email);

        return ResponseEntity.ok(new EmailValidationResponse(!isEmailTaken));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }

        final CustomUserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String accessToken = jwtUtil.generateToken(userDetails, userDetails.getProvider());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails, userDetails.getProvider());

        // Set refresh token as HttpOnly cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenDuration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthenticationResponse(accessToken));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody TokenVerificationRequest request) {
        String username = jwtUtil.extractUsername(request.getToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(request.getToken(), userDetails)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Refresh token endpoint hit");

        // Get refresh token from cookie
        Cookie[] cookies = request.getCookies();
        logger.info("Cookies received: " + (cookies != null ? cookies.length : "null"));

        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                logger.info("Cookie: " + cookie.getName() + "=" + cookie.getValue());
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            logger.warn("Refresh token is missing in the request");
            return ResponseEntity.badRequest().body("Refresh token is missing");
        }

        try {
            String username = jwtUtil.extractUsername(refreshToken);
            CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtUtil.generateToken(userDetails, userDetails.getProvider());
                String newRefreshToken = jwtUtil.generateRefreshToken(userDetails, userDetails.getProvider());

                // Set new refresh token as HttpOnly cookie
                Cookie newCookie = new Cookie("refreshToken", newRefreshToken);
                newCookie.setHttpOnly(true);
                newCookie.setSecure(true);
                newCookie.setPath("/");
                newCookie.setMaxAge(refreshTokenDuration);
                response.addCookie(newCookie);

                return ResponseEntity.ok(new AuthenticationResponse(newAccessToken));
            } else {
                return ResponseEntity.badRequest().body("Invalid refresh token");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing refresh token");
        }
    }

    @GetMapping("/debug")
    public ResponseEntity<String> debug() {
        logger.info("Debug endpoint reached in AuthController");
        return ResponseEntity.ok("AuthController debug endpoint reached");
    }
}
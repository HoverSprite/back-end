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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


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
    private final PersonService personService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          CustomUserDetailsService userDetailsService, PersonService personService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.personService = personService;
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

        return ResponseEntity.ok(newPerson);
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

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String accessToken = jwtUtil.generateToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Set refresh token as HttpOnly cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // if using HTTPS
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok(new AuthenticationResponse(accessToken));
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
        // Get refresh token from cookie
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token is missing");
        }

        try {
            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtUtil.generateToken(userDetails);
                String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

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
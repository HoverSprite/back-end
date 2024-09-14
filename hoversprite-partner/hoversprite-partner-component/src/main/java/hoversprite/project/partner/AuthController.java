package hoversprite.project.partner;


import hoversprite.project.jwt.*;
import hoversprite.project.request.PersonRequest;
import hoversprite.project.response.EmailValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> signin(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
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
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String username = jwtUtil.extractUsername(request.getToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(request.getToken(), userDetails)) {
            String newToken = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthenticationResponse(newToken));
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }

    @GetMapping("/debug")
    public ResponseEntity<String> debug() {
        logger.info("Debug endpoint reached in AuthController");
        return ResponseEntity.ok("AuthController debug endpoint reached");
    }
}
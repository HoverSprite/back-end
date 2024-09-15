package hoversprite.project.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        logger.info("JwtRequestFilter processing request to: " + request.getRequestURI());

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        String refreshToken = null;

        // Check for access token in Authorization header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("Error extracting username from access token", e);
            }
        }

        // Check for refresh token in cookies
        Cookie[] cookies = request.getCookies();
        logger.info("Cookies in request: " + (cookies != null ? cookies.length : "null"));
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                logger.info("Cookie: " + cookie.getName() + "=" + cookie.getValue());
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                    logger.info("Refresh token found in cookie");
                    break;
                }
            }
        }

        if (username == null && refreshToken != null) {
            try {
                username = jwtUtil.extractUsername(refreshToken);
            } catch (Exception e) {
                logger.error("Error extracting username from refresh token", e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt != null ? jwt : refreshToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // If we authenticated with refresh token, generate a new access token
                if (jwt == null && refreshToken != null) {
                    String newAccessToken = jwtUtil.generateToken(userDetails);
                    response.setHeader("Authorization", "Bearer " + newAccessToken);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
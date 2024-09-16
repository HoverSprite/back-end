package hoversprite.project.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        logger.info("JwtRequestFilter processing request to: {}", request.getRequestURI());

        if (request.getRequestURI().contains("/oauth2/") || request.getRequestURI().contains("/login/")) {
            chain.doFilter(request, response);
            return;
        }
        try {
            Optional<String> username = extractUsername(request);
            username.ifPresent(name -> authenticateUser(name, request, response));
        } catch (Exception e) {
            logger.error("Error in JwtRequestFilter", e);
        }

        chain.doFilter(request, response);
    }

    private Optional<String> extractUsername(HttpServletRequest request) {
        Optional<String> accessTokenUsername = extractUsernameFromHeader(request);
        return accessTokenUsername.isPresent() ? accessTokenUsername : extractUsernameFromCookie(request);
    }

    private Optional<String> extractUsernameFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String jwt = authHeader.substring(BEARER_PREFIX.length());
            return Optional.ofNullable(jwtUtil.extractUsername(jwt));
        }
        return Optional.empty();
    }

    private Optional<String> extractUsernameFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    logger.info("Refresh token found in cookie");
                    return Optional.ofNullable(jwtUtil.extractUsername(cookie.getValue()));
                }
            }
        }
        return Optional.empty();
    }

    private void authenticateUser(String username, HttpServletRequest request, HttpServletResponse response) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            String token = Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
                    .filter(header -> header.startsWith(BEARER_PREFIX))
                    .map(header -> header.substring(BEARER_PREFIX.length()))
                    .orElseGet(() -> findRefreshToken(request));

            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                if (!token.equals(findRefreshToken(request))) {
                    String newAccessToken = jwtUtil.generateToken(userDetails, userDetails.getProvider());
                    response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken);
                }
            }
        }
    }

    private String findRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
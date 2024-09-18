package hoversprite.project.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DebugFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(DebugFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
//        logger.info("DebugFilter - Request URI: {}", request.getRequestURI());
//        logger.info("DebugFilter - Request Method: {}", request.getMethod());
//        logger.info("DebugFilter - Authentication: {}", SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);

//        logger.info("DebugFilter - Response Status: {}", response.getStatus());
    }
}
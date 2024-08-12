package com.truongphuc.filter;

import com.truongphuc.constant.TokenType;
import com.truongphuc.service.JwtService;
import com.truongphuc.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {
    JwtService jwtService;
    UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("=============== PreFilter ===============");
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.isBlank())
        {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        if (token.isBlank())
        {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            boolean isValid = jwtService.verify(TokenType.ACCESS_TOKEN, token);

            if (!isValid) {
                filterChain.doFilter(request, response);
                return;
            } else if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = jwtService.extractEmail(TokenType.ACCESS_TOKEN,token);
                UserDetails userDetails = userService.getUserDetailsService().loadUserByUsername(email);

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
        }catch (Exception e){
            log.error("Error while validating token", e);
        }

        filterChain.doFilter(request, response);
    }

}
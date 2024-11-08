package com.truongphuc.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.constant.TokenType;
import com.truongphuc.dto.response.ApiResponse;
import com.truongphuc.exception.AppException;
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

//        log.info("=============== PreFilter ===============");

        try {
            String authHeader = request.getHeader("Authorization");
//            log.info("authHeader: {}", authHeader);
            if (authHeader ==  null){
                filterChain.doFilter(request, response);
                return;
            }

            if (!authHeader.startsWith("Bearer ") || authHeader.isBlank())
            {
                throw new AppException("Token is required", ExceptionCode.INVALID_TOKEN);
            }

            String token = authHeader.substring("Bearer ".length());

            if (token.isBlank())
            {
                throw new AppException("Token is required", ExceptionCode.INVALID_TOKEN);
            }

            boolean isValid = jwtService.verify(TokenType.ACCESS_TOKEN, token);


            if (isValid && SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = jwtService.extractEmail(TokenType.ACCESS_TOKEN,token);
                UserDetails userDetails = userService.getUserDetailsService().loadUserByUsername(email);

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }else{
                throw new AppException("Invalid token", ExceptionCode.INVALID_TOKEN);
            }
        }catch (AppException e){
            log.error("Error while validating token", e);
            response.setStatus(e.getHttpCode().value());
            response.setContentType("application/json");

            ApiResponse<?> result = ApiResponse.builder()
                    .code(e.getCode())
                    .message(e.getMessage())
                    .build();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), result);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
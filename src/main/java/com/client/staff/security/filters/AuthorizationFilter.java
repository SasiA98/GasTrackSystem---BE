package com.client.staff.security.filters;

import com.client.staff.security.models.JwtAuthentication;
import com.client.staff.security.services.CryptoService;
import com.client.staff.security.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CryptoService cryptoService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedJwt = authorizationHeader.substring(7);
            String jwt = cryptoService.decrypt(encryptedJwt);
            if (isAuthenticationNeeded(jwt)) {
                setAuthenticationInContext(buildAuthenticationToken(jwt, request));
            }
        }
        filterChain.doFilter(request, response);
    }

    private Authentication buildAuthenticationToken(String jwt, HttpServletRequest request) {
        JwtAuthentication jwtAuthentication = jwtService.deserialize(jwt);
        jwtAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        jwtAuthentication.setAuthenticated(true);
        return jwtAuthentication;
    }

    private Authentication getAuthenticationFromContext() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private void setAuthenticationInContext(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isAuthenticationNeeded(String jwt) {
        return jwt.length() > 0 &&
                getAuthenticationFromContext() == null &&
                jwtService.isTokenWellFormed(jwt) &&
                jwtService.isTokenNotExpired(jwt);
    }

}
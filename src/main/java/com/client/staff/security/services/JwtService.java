package com.client.staff.security.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.client.staff.security.configs.JwtServiceConfig;
import com.client.staff.security.models.JwtAuthentication;
import com.client.staff.security.models.UserSecurityDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JwtService {

    private final ObjectMapper objectMapper;
    private final CryptoService cryptoService;
    private final JwtParser jwtParser;
    private final JwtServiceConfig jwtServiceConfig;

    public JwtService(JwtServiceConfig jwtServiceConfig, CryptoService cryptoService, ObjectMapper objectMapper) throws Exception {
        this.jwtServiceConfig = jwtServiceConfig;
        this.objectMapper = objectMapper;
        this.cryptoService = cryptoService;
        this.jwtParser = Jwts.parser().setSigningKey(jwtServiceConfig.getSecretKey());
    }


    public boolean isTokenWellFormed(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public JwtAuthentication deserialize(String jwt) {
        Claims claims = extractAllClaims(jwt);
        return objectMapper.convertValue(claims, JwtAuthentication.class);
    }

    public boolean isTokenNotExpired(String token){
        JwtAuthentication jwtAuthentication = deserialize(token);
        return token != null && jwtAuthentication.getExpirationDate().after(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = buildClaims(userDetails);
        return buildToken(userDetails.getUsername(), claims);
    }

    public boolean isTokenAboutToExpire(JwtAuthentication jwtAuthentication) {
        Date tokenExpirationDate = jwtAuthentication.getExpirationDate();
        Date aboutToExpireDate = calculateAboutToExpireDate(tokenExpirationDate);
        Date currentDate = new Date();
        return currentDate.after(aboutToExpireDate) && currentDate.before(tokenExpirationDate);
    }

    public Optional<String> extractTokenFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            return Optional.of(authorizationHeader.substring(7));
        }
        return Optional.empty();
    }

    private Date calculateAboutToExpireDate(Date expirationDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expirationDate);
        calendar.add(Calendar.MILLISECOND, -jwtServiceConfig.getTokenRenewBeforeExpirationMs());
        return calendar.getTime();
    }

    private Map<String, Object> buildClaims(UserDetails userDetails) {
        var claims = new HashMap<String, Object>();
        claims.put("authorities", userDetails.getAuthorities());
        if (userDetails instanceof UserSecurityDetails) {
            claims.put("id", ((UserSecurityDetails) userDetails).getId());
        }
        return claims;
    }

    private String buildToken(String subject, Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtServiceConfig.getTokenDurationMs()))
                .signWith(SignatureAlgorithm.HS256, jwtServiceConfig.getSecretKey())
                .compact();

        return cryptoService.encrypt(jwt);
    }

    private Claims extractAllClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

}

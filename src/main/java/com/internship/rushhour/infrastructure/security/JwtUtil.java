package com.internship.rushhour.infrastructure.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwtutil.secretkey}")
    private String secretKey;

    @Value("${jwtutil.expiration}")
    private Long expiration;

    @Value("Bearer")
    private String tokenType;

    public String extractUsername(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();

        return claims.getSubject();
    }

    public Date extractExpiration(String jwt) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody().getExpiration();
    }

    public boolean validateToken(String jwt) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwt);

            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature ", e);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            LOGGER.error("Expired JWT token ", e);
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token ", e);
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Unsupported JWT token ", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims are missing ", e);
        }

        return false;
    }

    public String createToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date creationTime = new Date();
        Date expirationTime = new Date(creationTime.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(creationTime)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}

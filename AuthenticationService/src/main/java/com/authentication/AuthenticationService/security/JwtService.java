package com.authentication.AuthenticationService.security;

import com.authentication.AuthenticationService.models.Role;
import com.authentication.AuthenticationService.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Autowired
    private final UserDetailsService userDetailsService;

    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    public JwtService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public String extractEmail(String token) {
        try {
            return extractClaim(token, claims -> claims.get("email", String.class));
        } catch (Exception e) {
            logger.error("Error extracting email from token: {}", e.getMessage());
            throw e;
        }
    }

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage());
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            logger.error("Error extracting claim from token: {}", e.getMessage());
            throw e;
        }
    }

    public String generateToken(UserDetails userDetails) {
        try {
            return generateToken(new HashMap<>(), userDetails);
        } catch (Exception e) {
            logger.error("Error generating token for user: {}", userDetails.getUsername(), e);
            throw e;
        }
    }

    public String generateToken(Map<String, String> extraClaims, UserDetails userDetails) {
        try {
            if (userDetails instanceof User) {
                extraClaims.put("email", ((User) userDetails).getEmail());
            }
            return Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .claim("roles", userDetails.getAuthorities())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // 1 day
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating token with extra claims for user: {}", userDetails.getUsername(), e);
            throw e;
        }
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
            throw e;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (Exception e) {
            logger.error("Error extracting expiration date from token: {}", e.getMessage());
            throw e;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            token = token.trim();
            token = token.replace(" ", "");
            return Jwts.parser()
                    .setSigningKey(getSignInKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Error extracting all claims from token: {}", e.getMessage());
            throw e;
        }
    }

    private Key getSignInKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            logger.error("Error getting signing key: {}", e.getMessage());
            throw e;
        }
    }

    public String generateRefresh(Map<String, Objects> extraClaims, UserDetails userDetails) {
        try {
            return Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 604800000)) // 7 days
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating refresh token for user: {}", userDetails.getUsername(), e);
            throw e;
        }
    }

    public String getEmailFromToken(String token) {
        try {
            return extractEmail(token);
        } catch (Exception e) {
            logger.error("Error extracting email from token: {}", e.getMessage());
            throw e;
        }
    }

    public Role getRoleFromToken(String token) {
        try {
            return extractClaim(token, claims -> {
                List<Map<String, Object>> rolesList = (List<Map<String, Object>>) claims.get("roles");
                Map<String, Object> roleMap = rolesList.get(0);
                String role = (String) roleMap.get("authority");
                return Role.valueOf(role);
            });
        } catch (Exception e) {
            logger.error("Error extracting role from token: {}", e.getMessage());
            throw e;
        }
    }

    public Date getExpirationTimeFromToken(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (Exception e) {
            logger.error("Error extracting expiration time from token: {}", e.getMessage());
            throw e;
        }
    }

    public Boolean validateToken(String token) {
        try {
            String userEmail = extractEmail(token);
            if (StringUtils.isNotEmpty(userEmail) && !isTokenExpired(token)) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                return isTokenValid(token, userDetails);
            }
            return false;
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }
}

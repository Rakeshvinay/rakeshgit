 
package com.brihathi.Multi_Tenant.service;
 
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
 
import com.brihathi.Multi_Tenant.entity.User;
import com.brihathi.Multi_Tenant.entity.Educator;
 
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
 
@Service
public class JwtService {
 
    @Value("${security.jwt.secret-key}")
    private String secretKey;
 
    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;
 
    // ================== EXTRACT ==================
 
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
 
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }
 
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
 
 
    public Long extractUserId(String token) {
 
        Claims claims = extractAllClaims(token);
   
        Object userIdObj = claims.get("userId");
   
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        }
   
        if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
   
        return Long.parseLong(userIdObj.toString());
    }
    // ================== GENERATE ==================
 
    public String generateToken(UserDetails userDetails) {
 
        Map<String, Object> claims = new HashMap<>();
 
        // -------- USER --------
        if (userDetails instanceof User user) {
            claims.put("userType", "USER");
            claims.put("userId", user.getUserId());
            claims.put("enrollmentId", user.getEnrollmentId());
            claims.put("phoneNumber", user.getPhoneNumber());
            claims.put("tenantId", user.getTenantId());
            claims.put("batch",user.getBatch());
            claims.put("branch",user.getBranch());
            claims.put("name",user.getName());
 
            return buildToken(claims, user.getEnrollmentId());
        }
 
        // -------- EDUCATOR --------
        if (userDetails instanceof Educator educator) {
            claims.put("userType", "EDUCATOR");
            claims.put("educatorId", educator.getEducatorId());
            claims.put("educatorName", educator.getEducatorName());
            claims.put("email", educator.getEmail());
            claims.put("phoneNumber", educator.getPhoneNumber());
            claims.put("tenantId", educator.getTenantId());
            claims.put("subject", educator.getSubject());
            claims.put("status", educator.getStatus());
 
            return buildToken(claims, educator.getEmail());
        }
 
        throw new IllegalArgumentException("Unsupported user type");
    }
 
    public long getExpirationTime() {
                return jwtExpiration;
            }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }
 
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
 
    // ================== INTERNAL ==================
 
    private String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
 
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }








    public String extractUserType(String token) {
        return extractClaim(token, c -> c.get("userType", String.class));
    }
    
    public Long extractEducatorId(String token) {
    
        Claims claims = extractAllClaims(token);
    
        Object obj = claims.get("educatorId");
    
        if (obj == null) return null;
    
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        if (obj instanceof Long) return (Long) obj;
    
        return Long.parseLong(obj.toString());
    }
}
 
 
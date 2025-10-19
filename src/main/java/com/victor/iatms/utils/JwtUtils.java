package com.victor.iatms.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JwtUtils {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param email 用户邮箱
     * @return JWT令牌
     */
    public String generateToken(Integer userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        claims.put("email", email);
        return createToken(claims, email);
    }
    
    /**
     * 创建令牌
     * @param claims 数据声明
     * @param subject 主题
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    /**
     * 从令牌中获取数据声明
     * @param token 令牌
     * @return 数据声明
     */
    public Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * 从令牌中获取用户ID
     * @param token 令牌
     * @return 用户ID
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("user_id", Integer.class);
    }
    
    /**
     * 从令牌中获取用户邮箱
     * @param token 令牌
     * @return 用户邮箱
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
    
    /**
     * 验证令牌是否过期
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * 验证令牌
     * @param token 令牌
     * @return 是否有效
     */
    public Boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}

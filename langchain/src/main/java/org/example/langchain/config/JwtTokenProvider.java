package org.example.langchain.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {
    // 添加日志记录器
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret:default-secret-key-for-development-only-which-should-be-at-least-32-chars}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24小时
    private long expirationTime;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        try {
            // 确保密钥至少32个字符
            if (secretKey == null || secretKey.length() < 32) {
                throw new IllegalArgumentException("JWT密钥必须至少32个字符长");
            }
            
            // 使用正确的UTF-8编码
            key = Keys.hmacShaKeyFor(secretKey.getBytes("UTF-8"));
            
            logger.info("JWT token provider初始化成功");
        } catch (Exception e) {
            logger.error("初始化JWT token provider失败: {}", e.getMessage());
            throw new RuntimeException("初始化JWT token provider失败", e);
        }
    }

    // 生成Token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key) // 使用SecretKey对象签名，而不是字符串
                .compact();
    }

    // 从Token中获取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从Token中获取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 使用SecretKey对象验证
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 验证Token是否过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 验证Token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

package com.workspace.llmsystem.security.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *phương thức bổ trợ cho xử lý JWT sử dụng thuật toán HS512
 */
public class JwtTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    private String generateToken(Map<String, Object> claims){
        return Jwts.builder()
                .subject(claims.get(CLAIM_KEY_USERNAME).toString())
                .claim(CLAIM_KEY_CREATED, claims.get(CLAIM_KEY_CREATED).toString())
                .expiration(generateExpirationDate())
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }
    private Date generateExpirationDate(){
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }
    public  Claims getClaimsFromToken(String token){
        Claims claims = null;
        try{
            claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch(Exception e){
            LOGGER.error("Failed to get claims", e);
        }
        return claims;
    }
    public String getUsernameFromToken(String token){
        String username;
        try{
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch(Exception e){
            username = null;
        }
        return username;
    }
    public boolean validateToken(String token, UserDetails userDetails){
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
    private boolean isTokenExpired(String token){
        Date expiredDate = getExpirationDateFromToken(token);
        return expiredDate.before(new Date());
    }
    private Date getExpirationDateFromToken(String token){
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }
    public String refreshHeadToken(String oldToken){
        if(StrUtil.isEmpty(oldToken)){
            return null;
        }
        String token = oldToken.substring(tokenHead.length());
        if(StrUtil.isEmpty(token)){
            return null;
        }
        Claims claims = getClaimsFromToken(token);
        if(claims == null)
            return null;
        if(isTokenExpired(token))
            return null;
        if(tokenRefreshJustBefore(token, 30 * 60)){
            return token;
        }
        return generateToken(claims);
    }
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }
    private boolean tokenRefreshJustBefore(String token, int time){
        Claims claims = getClaimsFromToken(token);
        Date created = claims.get(CLAIM_KEY_CREATED, Date.class);
        Date refreshDate = new Date();
        if(refreshDate.after(created) && refreshDate.before(DateUtil.offsetSecond(created, time))) {
            return true;
        }
        return false;
    }
}

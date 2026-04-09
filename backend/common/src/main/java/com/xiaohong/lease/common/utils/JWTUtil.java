package com.xiaohong.lease.common.utils;

import com.xiaohong.lease.common.exception.LeaseException;
import com.xiaohong.lease.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JWTUtil {


    private static Long expire = 365 * 24 * 60 * 60 * 1000L;

    private static Key key = Keys.hmacShaKeyFor("VP8ZQBfj0q2SRXAmBMYIbCbL9znlKNTc".getBytes());

    public static String createToken(Long userId, String username) {

        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .setSubject("LOGIN_USER")
                .claim("userId", userId)
                .claim("username", username)
                .signWith(key).compact();
    }


    public static Claims parseToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().
                    setSigningKey(key).
                    build().parseClaimsJws(token);
            return claimsJws.getBody();

        } catch (ExpiredJwtException e) {
            throw new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new LeaseException(ResultCodeEnum.TOKEN_INVALID);
        }
    }

    public static void main(String[] args) {
        System.out.println(createToken(1L, "13888888888"));
        System.out.println(createToken(1L, "admin"));
    }

}

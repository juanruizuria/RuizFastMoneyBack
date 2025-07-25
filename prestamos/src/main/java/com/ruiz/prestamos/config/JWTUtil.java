package com.ruiz.prestamos.config;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Component
public class JWTUtil {
    private static final String KEY = "juanca";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(KEY);

    public String generarToken(String username) {
        Calendar expira = Calendar.getInstance();
        expira.setTime(new Date());
        //expira.add(Calendar.HOUR_OF_DAY, 10);
        expira.add(Calendar.MINUTE, 10);
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(expira.getTime())
                .sign(ALGORITHM);
    }

    public String getUsername(String token) {
        return JWT.require(ALGORITHM).build().verify(token).getSubject();
    }

    public String getToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public boolean validarToken(String token) {
        try {
            JWT.require(ALGORITHM).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = JWT.require(ALGORITHM).build().verify(token).getExpiresAt();
            return expirationDate.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

}

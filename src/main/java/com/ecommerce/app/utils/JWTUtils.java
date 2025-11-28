package com.ecommerce.app.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ecommerce.app.exceptions.APIException;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

public class JWTUtils {

    private static final Algorithm algorithm;

    static {
        Dotenv dotenv = Dotenv.load();
        String JWT_SECRET_KEY = dotenv.get("JWT_SECRET_KEY");
        algorithm = Algorithm.HMAC256(JWT_SECRET_KEY);
    }

    public static String getJWT(HashMap<String, Object> claims, Duration duration) {
        Instant issued = Instant.now();
        Instant expires = issued.plus(duration);

        return JWT.create()
                .withPayload(claims)
                .withIssuedAt(issued)
                .withExpiresAt(expires)
                .sign(algorithm);
    }

    public static String getJWT(HashMap<String, Object> claims) {
        return getJWT(claims, Duration.ofMinutes(15));
    }

    public static DecodedJWT verifyJWT(String token) {
        try {
            return JWT.require(algorithm).build().verify(token);
        } catch (JWTVerificationException e) {
            throw new APIException(401, e);
        }

    }

}

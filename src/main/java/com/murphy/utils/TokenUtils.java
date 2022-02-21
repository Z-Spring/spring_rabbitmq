package com.murphy.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Map;

/**
 * token utils
 * @author Murphy
 */
@Component
public class TokenUtils {
    public static final String TOKEN_KEY = "0-=12hu7^%$0sdaf";

    public static String getToken(Map<String, String> claims) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        JWTCreator.Builder builder = JWT.create();
//        payload
        claims.forEach((k, v) -> {
            builder.withClaim(k, v);
        });
        String token = builder.withExpiresAt(calendar.getTime())
                .sign(Algorithm.HMAC256(TOKEN_KEY));

        return token;
    }

    /**
     * 验证token合法性
     * @return
     */
/*    public static void verifyToken( String token){
        JWT.require(Algorithm.HMAC256("secret")).build().verify(token);
    }*/

    /**
     * 验证、解析token
     * @param token
     * @return
     */
    public static DecodedJWT decodeToken(String token){
        return JWT.decode(token);
    }
}

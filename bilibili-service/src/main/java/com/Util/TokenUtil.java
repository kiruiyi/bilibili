package com.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

public class TokenUtil {

    private static final String ISSUER = "这是签发者";


    //TODO 为 userId  生成Token 使用RSA加密
    public static String generateToken(Long userId) throws Exception {

        //TODO 公钥 密钥   加密算法
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 30);
        return JWT.create()
                //TODO 唯一标识ID
                .withKeyId(String.valueOf(userId))
                //TODO 签发者
                .withIssuer(ISSUER)
                //TODO 过期时间
                .withExpiresAt(calendar.getTime())
                //TODO 生成 TOKEN
                .sign(algorithm);
    }

    //TODO 解密Token  返回用户Id
    public static Long verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).build();

            //TODO 返回解密后的 JWT
            DecodedJWT jwt = verifier.verify(token);

            return Long.parseLong(jwt.getKeyId());

        } catch (TokenExpiredException e) {  //TODO Token过期异常
            throw new ConditionException("555", "token过期！");
        } catch (Exception e) {
            throw new ConditionException("非法用户token!");
        }

    }

    public static String generateRefreshToken(Long userId) throws Exception {
        //TODO 公钥 密钥   加密算法
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        return JWT.create()
                //TODO 唯一标识ID
                .withKeyId(String.valueOf(userId))
                //TODO 签发者
                .withIssuer(ISSUER)
                //TODO 过期时间
                .withExpiresAt(calendar.getTime())
                //TODO 生成 TOKEN
                .sign(algorithm);
    }
}

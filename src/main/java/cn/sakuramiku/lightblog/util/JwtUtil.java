package cn.sakuramiku.lightblog.util;

import cn.sakuramiku.lightblog.entity.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.*;

/**
 * JWT校验工具类
 * <ol>
 * <li>iss: jwt签发者</li>
 * <li>sub: jwt所面向的用户</li>
 * <li>aud: 接收jwt的一方</li>
 * <li>exp: jwt的过期时间，这个过期时间必须要大于签发时间</li>
 * <li>nbf: 定义在什么时间之前，该jwt都是不可用的</li>
 * <li>iat: jwt的签发时间</li>
 * <li>jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击</li>
 * </ol>
 */
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * 签发者
     */
    private static final String ISS = "sakuramiku.cn";
    /**
     * 密钥
     */
    private static final String SECRET;
    /**
     * 过期时间,秒
     */
    private static final long EXPIRE;

    static {
        ClassPathResource resource = new ClassPathResource("base-conf.properties");
        Properties properties = new Properties();
        try {
            properties.load(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String sec = properties.getProperty("jwt.secret", "sakuramiku.cn");
        String exp = properties.getProperty("jwt.expire", "3600");

        SECRET = sec;
        EXPIRE = Long.parseLong(exp) * 1000;
    }


    /**
     * 快速生成Token
     *
     * @param user 用户
     * @return
     */
    public static String genToken(User user) {
        if (null == user) {
            return null;
        }
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("nickName", user.getNickName());
        return genToken(claims, String.valueOf(user.getId()));
    }

    /**
     * 生成Token
     *
     * @param claims 自定义内容
     * @param id     唯一标识
     * @return Token
     */
    public static String genToken(Map<String, Object> claims, String id) {
        return genToken(claims, id, "sakuramiku.cn");
    }

    /**
     * 生成Token
     *
     * @param claims  自定义内容
     * @param id      唯一标识
     * @param subject 所有者
     * @return token
     */
    public static String genToken(Map<String, Object> claims, String id, String subject) {
        Date createdTime = new Date();
        Date expirationTime = getTokenExpire();
        byte[] keyBytes = SECRET.getBytes();
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        return Jwts.builder()
                .setId(id)
                .setIssuer(ISS)
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(createdTime)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    /**
     * 从token中获取claim
     *
     * @param token token
     * @return claim 自定义内容
     */
    public static Claims getClaims(@NonNull String token) {
        try {
            byte[] keyBytes = SECRET.getBytes();
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            return Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e1) {
            logger.error("Token已过期{{}}", token);
            throw e1;
        } catch (Exception e2) {
            logger.error("无效的Token{{}}", token);
            throw e2;
        }
    }

    public static String getUserName(@NonNull String token) {
        Claims claims = getClaims(token);
        if (null == claims) {
            return null;
        }
        return claims.get("username", String.class);
    }

    /**
     * 获取token的过期时间
     *
     * @param token token
     * @return 过期时间
     */
    public static Date getTokenExpire(@NonNull String token) {
        return Objects.requireNonNull(getClaims(token))
                .getExpiration();
    }

    /**
     * @param token
     * @return
     */
    public static boolean isTokenExpired(String token) {
        Date expire = getTokenExpire(token);
        return new Date().after(expire);
    }

    /**
     * 计算token的过期时间
     *
     * @return 过期时间
     */
    private static Date getTokenExpire() {
        return new Date(System.currentTimeMillis() + EXPIRE);
    }

}

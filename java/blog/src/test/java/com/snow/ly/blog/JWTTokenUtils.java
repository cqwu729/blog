package com.snow.ly.blog;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

public class JWTTokenUtils {




    private final Logger log = LoggerFactory.getLogger(JWTTokenUtils.class);

    private static final String AUTHORITIES_KEY = "userId";

    private static final String secretKey="blog";           //签名密钥

    private static final int secondIn1day = 1000 * 60 * 60 * 24;

    private static final long tokenValidityInMilliseconds=secondIn1day * 2L;       //失效日期

    private static final long tokenValidityInMillisecondsForRememberMe=secondIn1day * 7L;      //（记住我）失效日期




    //创建Token
    public String createToken(String userId, Boolean rememberMe){
        long now = (new Date()).getTime();              //获取当前时间戳
        Date validity;                                          //存放过期时间
        if (rememberMe){
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }else {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        }

        return Jwts.builder()                                   //创建Token令牌
                .setSubject(userId)                             //设置面向用户
                .claim(AUTHORITIES_KEY,userId)                  //添加权限属性
                .setExpiration(validity)                        //设置失效时间
                .signWith(SignatureAlgorithm.HS512,secretKey)   //生成签名
                .compact();
    }


    //获取用户id
    public String getUserId(String token){

        Claims claims = Jwts.parser()                           //解析Token的payload
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

      return  claims.get(AUTHORITIES_KEY).toString();
    }


    //验证Token是否正确
    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);   //通过密钥验证Token
            return true;
        }catch (SignatureException e) {                                     //签名异常
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {                                 //JWT格式错误
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {                                   //JWT过期
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {                               //不支持该JWT
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {                              //参数错误异常
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }





    public static void main(String[]args){

        JWTTokenUtils jwt=new JWTTokenUtils();

        String token=jwt.createToken("xxxxxxxxx",true);
        System.out.println(token);
        if (jwt.validateToken(token)){
            System.out.println(jwt.getUserId(token));
        }else{
            System.out.println("ssssssssssssssssssssssss");
        }

    }




}

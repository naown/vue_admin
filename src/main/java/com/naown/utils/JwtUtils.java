package com.naown.utils;

import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author chenjian
 */
@Data
@Component
@ConfigurationProperties(prefix = "naown.jwt")
public class JwtUtils {

	private long expire;
	private String secret;
	private String header;

	/**
	 * 生成jwt
	 */
	public String generateToken(String username) {

		Date nowDate = new Date();
		Date expireDate = new Date(nowDate.getTime() + 1000 * expire);

		return Jwts.builder()
				.setHeaderParam("typ", "JWT")
				.setSubject(username)
				.setIssuedAt(nowDate)
				// 7天过期
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	/**
	 * 解析jwt
	 * @param jwt
	 * @return
	 */
	public Claims getClaimByToken(String jwt) {
		try {
			return Jwts.parser()
					.setSigningKey(secret)
					.parseClaimsJws(jwt)
					.getBody();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 检测jwt是否过期
	 * @param claims
	 * @return
	 */
	public boolean isTokenExpired(Claims claims) {
		return claims.getExpiration().before(new Date());
	}

}

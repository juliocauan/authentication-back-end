package br.com.juliocauan.authentication.infrastructure.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

	@Value("${auth.jwt.secret}")
	private String jwtSecret;

	@Value("${auth.jwt.expiration}")
	private Long jwtExpirationInMs;

	private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	public String generateToken(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
		return Jwts.builder()
				.setSubject(userPrincipal.getUsername())
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(key)
				.compact();
	}

	public String getUserUsernameFromJWT(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	public Boolean isTokenValid(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

}
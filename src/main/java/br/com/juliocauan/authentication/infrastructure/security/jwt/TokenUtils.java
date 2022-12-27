package br.com.juliocauan.authentication.infrastructure.security.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenUtils {

	@Value("${auth.jwt.secret}")
	private String secret;

	@Value("${auth.jwt.expiration}")
	private long expiration;

	public String generateToken(Authentication authentication) {
		UserEntity user = (UserEntity) authentication.getPrincipal();
		return Jwts.builder()
				.setIssuer("Auth API")
				.setSubject((user.getUsername()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + expiration))
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
	}

	public Boolean isTokenValid(String authToken) {
		try {
			Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}
}
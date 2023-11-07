package br.com.juliocauan.authentication.infrastructure.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;
import io.jsonwebtoken.Jwts;

@Component
public class JwtProvider {

	private static final long EXPIRATION = (20 * 60 * 1000);
	private static final SecretKey KEY = Jwts.SIG.HS256.key().build();

	public final String generateToken(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + EXPIRATION);
		return "Bearer " + Jwts.builder()
				.subject(userPrincipal.getUsername())
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(KEY)
				.compact();
	}

	public final String getUsernameFromJWT(String token) {
		return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public final boolean isTokenValid(String token) {
		try{
			Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

}
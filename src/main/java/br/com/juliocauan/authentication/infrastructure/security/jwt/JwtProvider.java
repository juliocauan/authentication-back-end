package br.com.juliocauan.authentication.infrastructure.security.jwt;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.openapitools.model.UserData;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class JwtProvider {

	private static final long EXPIRATION = (20 * 60 * 1000); // 20 minutes
	private static final SecretKey KEY = Jwts.SIG.HS256.key().build();

	public static UserData authenticate(String username, String password, AuthenticationManager authenticationManager) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
		return new UserData()
			.roles(getRoles(authentication))
			.JWT(generateJWT(authentication));
	}

	private static Set<String> getRoles(Authentication authentication) {
		return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
	}

	private static String generateJWT(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + EXPIRATION);
		return Jwts.builder()
				.subject(userPrincipal.getUsername())
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(KEY)
				.compact();
	}

	static void authenticate(UserDetails userDetails, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities());
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	static String getUsernameFromJWT(String token) {
		return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload().getSubject();
	}

	static boolean isTokenValid(String token) {
		try{
			Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	static String getJwtFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
        if(token == null || !token.startsWith("Bearer ")) return null;
        return token.substring(7);
	}

}
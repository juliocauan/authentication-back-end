package br.com.juliocauan.authentication.infrastructure.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.security.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TokenAuthFilter extends OncePerRequestFilter {

	private final TokenUtils tokenUtils;
	private final AuthenticationService authService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = parseToken(request);
		if (tokenUtils.isTokenValid(token)) authenticateUser(token, request);
		filterChain.doFilter(request, response);
	}

    private void authenticateUser(String token, HttpServletRequest request) {
		UserEntity userEntity = authService.loadUserByUsername(tokenUtils.getUsername(token));
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userEntity, null, userEntity.getAuthorities());
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
    }

	private String parseToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
        if(token == null || token.isEmpty() || !token.startsWith("Bearer ")) return null;
        return token.substring(7);
	}
}
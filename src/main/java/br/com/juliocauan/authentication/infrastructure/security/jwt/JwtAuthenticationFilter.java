package br.com.juliocauan.authentication.infrastructure.security.jwt;

import java.io.IOException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.juliocauan.authentication.infrastructure.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = JwtProvider.getJwtFromRequest(request);
		if (JwtProvider.isTokenValid(token)) authenticateUser(token, request);
		filterChain.doFilter(request, response);
	}

    private void authenticateUser(String token, HttpServletRequest request) {
		String username = JwtProvider.getUsernameFromJWT(token);
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		JwtProvider.authenticate(userDetails, request);
    }

}
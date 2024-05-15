package br.com.juliocauan.authentication.infrastructure.security.jwt;

import java.io.IOException;

import org.openapitools.model.AuthenticationError;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setContentType("application/json");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		String message = new ObjectMapper().writeValueAsString(new AuthenticationError().message(authException.getMessage()));
		response.getWriter().write(message);
	}

}

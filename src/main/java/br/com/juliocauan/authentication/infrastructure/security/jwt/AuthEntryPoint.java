package br.com.juliocauan.authentication.infrastructure.security.jwt;

import java.io.IOException;

import org.openapitools.model.CustomError;
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
				response.setStatus(403);
		
				ObjectMapper mapper = new ObjectMapper();
				response.getWriter().write(mapper.writeValueAsString(new CustomError()
					.code(501)
					.message("Bad Credentials!")
					.trace(null)
					.fieldList(null)));
	}

}

package br.com.juliocauan.authentication.infrastructure.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import br.com.juliocauan.authentication.infrastructure.security.jwt.AuthEntryPoint;
import br.com.juliocauan.authentication.infrastructure.security.jwt.JwtAuthenticationFilter;
import br.com.juliocauan.authentication.infrastructure.security.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class WebSecurityConfig {
    
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthFilter;
    
    private static final String URI_ROOT = "/api/auth";
    private static final String URI_SIGNUP = URI_ROOT + "/signup";
    private static final String URI_SIGNUP_ADMIN = URI_ROOT + "/signup/admin";
    private static final String URI_SIGNIN = URI_ROOT + "/signin";
    private static final String URI_FORGOT_PASSWORD = URI_ROOT + "/forgotpassword";

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandler));
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.authenticationProvider(authenticationProvider());
        http.cors(withDefaults()).csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST,
                        URI_SIGNUP,
                        URI_SIGNUP_ADMIN,
                        URI_SIGNIN,
                        URI_FORGOT_PASSWORD)
                    .permitAll()
                .requestMatchers(HttpMethod.PATCH, URI_FORGOT_PASSWORD + "/{token}").permitAll()
            .anyRequest().authenticated());
        return http.build();
    }
}
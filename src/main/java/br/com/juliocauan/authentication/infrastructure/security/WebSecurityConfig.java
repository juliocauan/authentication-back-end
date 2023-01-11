package br.com.juliocauan.authentication.infrastructure.security;

import org.openapitools.model.EnumRole;
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

import br.com.juliocauan.authentication.infrastructure.security.jwt.AuthEntryPoint;
import br.com.juliocauan.authentication.infrastructure.security.jwt.JwtAuthenticationFilter;
import br.com.juliocauan.authentication.infrastructure.security.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    // securedEnabled = true,
    // jsr250Enabled = true,
    prePostEnabled = true)
@AllArgsConstructor
public class WebSecurityConfig {
    
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthFilter;
    
    private final String user = EnumRole.USER.getValue();
    private final String admin = EnumRole.ADMIN.getValue();
    private final String moderator = EnumRole.MANAGER.getValue();
    
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
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.authenticationProvider(authenticationProvider());
        http.cors().and().csrf().disable();
        
        http.authorizeHttpRequests((authorize) -> authorize
            .requestMatchers(HttpMethod.POST, "/api/auth/signup", "/api/auth/signin").permitAll()
                .requestMatchers("/api/test/all").permitAll()
                .requestMatchers("/api/test/" + user).hasAnyRole(user, moderator, admin)
                .requestMatchers("/api/test/" + moderator).hasRole(moderator)
                .requestMatchers("/api/test/" + admin).hasRole(admin)
            .anyRequest().authenticated());

        return http.build();
    }
}
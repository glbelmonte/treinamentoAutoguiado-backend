package br.pucrs.ages.treinamentoautoguiado.api.config;

import br.pucrs.ages.treinamentoautoguiado.api.security.filter.JWTCheckFilter;
import br.pucrs.ages.treinamentoautoguiado.api.security.handler.CustomAccessDeniedHandler;
import br.pucrs.ages.treinamentoautoguiado.api.security.handler.CustomAuthenticationEntryPoint;
import br.pucrs.ages.treinamentoautoguiado.api.service.CustomUserDetailsService;
import br.pucrs.ages.treinamentoautoguiado.api.service.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@Log4j2
@EnableMethodSecurity
@AllArgsConstructor
public class CustomSecurityConfig {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS
        http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()));

        // CSRF
        http.csrf(AbstractHttpConfigurer::disable);

        // SESSION
        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // EXCEPTIONS
        http.exceptionHandling(exceptionHandling -> {
            exceptionHandling.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
            exceptionHandling.accessDeniedHandler(new CustomAccessDeniedHandler());
        });

        // AUTH
        http.authorizeHttpRequests(authorizeRequests -> {
            authorizeRequests.requestMatchers("/auth/**").permitAll();
            authorizeRequests.requestMatchers("/users").permitAll();
            authorizeRequests.requestMatchers("/swagger-ui/**").permitAll();
            authorizeRequests.requestMatchers("/v3/api-docs/**").permitAll();
            authorizeRequests.requestMatchers("/user/admin/**").hasAuthority("ADMIN");
            authorizeRequests.anyRequest().authenticated();
        });

        // JWT
        http.addFilterBefore(new JWTCheckFilter(jwtService, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

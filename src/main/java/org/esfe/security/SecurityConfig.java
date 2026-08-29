package org.esfe.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/login").permitAll()
                 .requestMatchers("/categorias-medicamentos/**").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "MEDICO", "ENCARGADO_INVENTARIO")
                 .requestMatchers("/medicamentos/**").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "MEDICO", "ENCARGADO_INVENTARIO")
                .requestMatchers("/usuarios", "/usuarios/**").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        response.sendRedirect("/panel"))
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/panel", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                    .logoutRequestMatcher(request ->
                            request.getServletPath().equals("/logout"))
                    .logoutSuccessUrl("/login?logout=true")
                    .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/css/**", "/js/**")
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PlainTextPasswordEncoder();
    }
}

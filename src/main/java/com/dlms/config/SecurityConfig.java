package com.dlms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration
 * Handles authentication + authorization for DLMS system.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // ==========================================
    // AUTHENTICATION PROVIDER
    // ==========================================
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // ==========================================
    // AUTHENTICATION MANAGER
    // ==========================================
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    // ==========================================
    // SECURITY FILTER CHAIN
    // ==========================================
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 1. Disable CSRF for REST API compatibility with fetch()
            .csrf(csrf -> csrf.disable())

            // 2. Register authentication provider
            .authenticationProvider(authProvider())

            // 3. AUTHORIZATION RULES
            .authorizeHttpRequests(auth -> auth

                // PUBLIC ASSETS & PAGES
                .requestMatchers(
                    "/", 
                    "/login", 
                    "/register", 
                    "/error",
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/api/auth/**"
                ).permitAll()
                
                .requestMatchers(
                        "/admin-dashboard", 
                        "/api/admin/**"
                ).hasAuthority("ADMIN")

                // OFFICER VIEWS & APIs (Combined to prevent 403 conflicts)
                .requestMatchers(
                    "/officer-dashboard/**", 
                    "/testing-dashboard/**", 
                    "/api/officer/**"
                ).hasAnyAuthority("LICENSING_OFFICER", "TESTING_OFFICER")

                // TRAFFIC POLICE API
                .requestMatchers("/api/enforcement/**")
                    .hasAuthority("TRAFFIC_POLICE")

                // APPLICANT DASHBOARD & CORE APPLICATION APIs
                .requestMatchers(
                    "/applicant-dashboard/**", 
                    "/api/applicant/**", 
                    "/api/applications/**"
                ).hasAuthority("APPLICANT")

                // CATCH-ALL: Any other request requires a valid login
                .anyRequest().authenticated()
            )

            // 4. LOGIN CONFIGURATION
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true) 
                .failureUrl("/login?error")
                .permitAll()
            )

            // 5. LOGOUT CONFIGURATION
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
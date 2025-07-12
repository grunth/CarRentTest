package com.example.demo_car_rent.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtSpec -> jwtSpec.jwtDecoder(jwtDecoder()))
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        String issuerUri = "http://localhost:8080/realms/demo";

        ReactiveJwtDecoder decoder = ReactiveJwtDecoders.fromIssuerLocation(issuerUri);

        return token -> decoder.decode(token)
                .doOnNext(jwt -> {
                    System.out.println("==== JWT получен ====");
                    System.out.println("Token: " + jwt.getTokenValue());
                    System.out.println("Subject: " + jwt.getSubject());
                    System.out.println("Issuer: " + jwt.getIssuer());
                    System.out.println("Claims: " + jwt.getClaims());
                });
    }
}
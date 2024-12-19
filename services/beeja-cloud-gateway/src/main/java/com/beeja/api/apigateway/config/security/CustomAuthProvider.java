package com.beeja.api.apigateway.config.security;

import com.beeja.api.apigateway.config.security.properties.JwtProperties;
import com.beeja.api.apigateway.user.UserEntity;
import com.beeja.api.apigateway.user.UserRepository;
import com.beeja.api.apigateway.utils.JwtUtil;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomAuthProvider implements ReactiveAuthenticationManager {

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private JwtProperties jwtProperties;

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    UserEntity user = userRepository.findByEmail(username);
    if (user == null || !user.isActive()) {
      return Mono.error(new BadCredentialsException("User Not Found"));
    }

    return Mono.just(user)
        .flatMap(
            userEntity -> {
              if (userEntity != null
                  && userEntity.isActive()
                  && passwordEncoder.matches(password, userEntity.getPassword())) {
                UserDetails userDetails =
                    User.withUsername(username)
                        .password(password)
                        .authorities(
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                        .build();
                String jwtToken =
                    JwtUtil.generateToken(authentication.getName(), jwtProperties.getSecret());
                return Mono.just(
                    new UsernamePasswordAuthenticationToken(
                        userDetails, jwtToken, userDetails.getAuthorities()));
              } else {
                return Mono.error(new BadCredentialsException("Invalid username or password"));
              }
            });
  }
}

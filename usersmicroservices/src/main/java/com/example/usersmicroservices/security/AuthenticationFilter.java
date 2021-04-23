package com.example.usersmicroservices.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.usersmicroservices.models.LoginRequestModel;
import com.example.usersmicroservices.services.UsersService;
import com.example.usersmicroservices.shared.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final UsersService usersService;
    private final Environment environment;
    private final AuthenticationManager authenticationManager;
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestModel requestModel = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestModel.getEmail(),
                            requestModel.getPassword(), new ArrayList<>())
            );
        }catch (IOException e){
            throw new RuntimeException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = authResult.getName();
        UserDto userDto = usersService.getUserDetailsByEmail(username);

        String token = JWT.create()
                .withSubject(userDto.getUserId())
                .withExpiresAt(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration_time")))).sign(Algorithm.HMAC512(environment.getProperty("token.secret")));

        response.addHeader("token", token);
        response.addHeader("userId", userDto.getUserId());
    }
}

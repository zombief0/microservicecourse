package com.example.apigateway;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * A class used to get the token and verify it before sending the
 * request to the microservice
 */
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    @Autowired
    private Environment environment;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
           ServerHttpRequest request = exchange.getRequest();
           if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
               return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
           }

           String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

           String jwt = authorizationHeader.replace("Bearer ","");
           if (!isJwtValid(jwt)){
               return onError(exchange, "Jwt token is not valid", HttpStatus.UNAUTHORIZED);
           }
           return chain.filter(exchange);

        });
    }

    private boolean isJwtValid(String jwt){
        boolean returnValue = true;
        String subject = null;
        try {
            subject = JWT.require(Algorithm.HMAC512(environment.getProperty("token.secret")))
                    .build().verify(jwt)
                    .getSubject();
        } catch (Exception e){
            returnValue = false;
        }


        if (subject == null || subject.isEmpty()){
            return false;
        }
        return returnValue;
    }

    public static class Config{

    }

    private Mono<Void> onError(ServerWebExchange exchange,
                               String err, HttpStatus httpStatus){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

}

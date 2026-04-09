package com.example.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange, "{\"error\":\"missing_token\", \"message\":\"Missing authorization header\"}", HttpStatus.UNAUTHORIZED);
                }

                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader == null || authHeader.isBlank()) {
                    return onError(exchange, "{\"error\":\"missing_token\", \"message\":\"Missing authorization header\"}", HttpStatus.UNAUTHORIZED);
                }
                if (authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                return webClientBuilder.build()
                        .get()
                        .uri("http://auth-service/api/v1/auth/validate")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authHeader)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(response -> exchange)
                        .flatMap(chain::filter)
                        .onErrorResume(WebClientResponseException.class, ex -> {
                            // Forward the error response body from auth-service to the client
                            return onError(exchange, ex.getResponseBodyAsString(), ex.getStatusCode());
                        })
                        .onErrorResume(Exception.class, e -> {
                            // Generic error handler for other issues (e.g., auth-service is down)
                            return onError(exchange, "{\"error\":\"auth_service_unavailable\", \"message\":\"Error while validating token\"}", HttpStatus.INTERNAL_SERVER_ERROR);
                        });
            }
            return chain.filter(exchange);
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatusCode httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = response.bufferFactory().wrap(err.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
    }
}

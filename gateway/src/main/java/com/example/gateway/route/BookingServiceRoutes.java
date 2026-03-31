package com.example.gateway.route;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;

@Configuration
public class BookingServiceRoutes {
    @Bean
    public RouterFunction<ServerResponse> bookingRoutes() {
        return GatewayRouterFunctions.route("booking-service")
                .route(RequestPredicates.POST("/api/v1/booking"),
                        HandlerFunctions.http("http://booking-service:8081/api/v1/booking"))
                .route(RequestPredicates.POST("/api/v1/auth/login"),
                        HandlerFunctions.http("http://booking-service:8081/api/v1/auth/login"))
                .route(RequestPredicates.POST("/api/v1/auth/register"),
                        HandlerFunctions.http("http://booking-service:8081/api/v1/auth/register"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("bookingServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoutes() {
        return GatewayRouterFunctions.route("fallbackRoute")
                .POST("/fallbackRoute",
                        request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Booking service is down!"))
                .build();
    }

    // OpenAPI route
    @Bean
    public RouterFunction<ServerResponse> bookingServiceApiDocs() {
        return GatewayRouterFunctions.route("booking-service-api-docs")
                .route(RequestPredicates.path("/docs/booking_service/v3/api-docs"),
                        HandlerFunctions.http("http://booking-service:8081"))
                .filter(setPath("/v3/api-docs"))
                .build();
    }
}

package com.canhxuan.cart_service.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorDecoder implements ErrorDecoder {
    private final Logger logger = LoggerFactory.getLogger(CustomErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                return new BadRequestException("Bad request to product service");
            case 404:
                return new RuntimeException("Product not found");
            case 503:
                return new ServiceUnavailableException("Product service unavailable");
            default:
                logger.error("Unknown error occurred: {}", response.status());
                return new Exception("Generic error");
        }
    }
}

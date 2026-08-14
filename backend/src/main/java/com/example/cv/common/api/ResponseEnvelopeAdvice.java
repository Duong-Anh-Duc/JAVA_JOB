package com.example.cv.common.api;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ResponseEnvelopeAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body == null || body instanceof ApiResponse<?> || body instanceof byte[]
                || body instanceof org.springframework.core.io.Resource
                || body instanceof GlobalExceptionHandler.ErrorResponse
                || selectedContentType == null
                || !selectedContentType.includes(MediaType.APPLICATION_JSON)) {
            return body;
        }

        ResponseMessage annotation = returnType.getMethodAnnotation(ResponseMessage.class);
        String message = annotation == null ? "" : annotation.value();
        int status = 200;
        if (response instanceof org.springframework.http.server.ServletServerHttpResponse servletResponse) {
            status = servletResponse.getServletResponse().getStatus();
        }
        return new ApiResponse<>(status, message, body);
    }
}

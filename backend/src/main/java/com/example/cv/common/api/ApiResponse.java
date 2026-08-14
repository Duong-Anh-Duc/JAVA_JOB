package com.example.cv.common.api;

public record ApiResponse<T>(int statusCode, String message, T data) {
}

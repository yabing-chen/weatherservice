package com.weather.demo.controller;

import com.weather.demo.domain.WeatherResponse;
import com.weather.demo.exception.WeatherException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({WeatherException.class, Exception.class})
    public ResponseEntity<WeatherResponse> handleException(Exception e, HttpServletRequest request){
        WeatherResponse res = WeatherResponse.builder()
                                .status("failed")
                                .error(e.getMessage())
                                .build();

        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(res);
    }
}

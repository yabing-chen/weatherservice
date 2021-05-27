package com.weather.demo.controller;

import com.weather.demo.domain.WeatherResponse;
import com.weather.demo.respository.OpenWeatherRepository;
import com.weather.demo.service.APIKeyService;
import com.weather.demo.service.WeatherForecastService;
import com.weather.demo.service.WeatherValidatorService;
import io.github.bucket4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RestController
public class WeatherForecastController {

    Logger logger = LoggerFactory.getLogger((WeatherForecastController.class));

    @Autowired
    private WeatherForecastService weatherForecastService;

    @Autowired
    private WeatherValidatorService weatherValidatorService;

    @Autowired
    private APIKeyService apiKeyService;

    @GetMapping(value="/api/v1/weather/{country}/{city}")
    public Callable<ResponseEntity<WeatherResponse>> weatherForecast(@PathVariable String country,
                                                                    @PathVariable String city,
                                                                    @RequestHeader Map<String, String> headers,
                                                                     HttpServletResponse response
                                                     ){
        return () ->
        {
            logger.info("Validate weather request");
            weatherValidatorService.validateRequest(headers, response);
            logger.info("Receive weather request for country: " + country + ", city: " + city);
            return weatherForecastService.getWeather(country, city);
        };

    }

}

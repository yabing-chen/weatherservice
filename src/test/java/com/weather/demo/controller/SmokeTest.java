package com.weather.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class SmokeTest {

    @Autowired
    private WeatherForecastController weatherForecastController;

    @Test
    public void contextLoad() throws Exception{
        assertThat(weatherForecastController).isNotNull();
    }
}

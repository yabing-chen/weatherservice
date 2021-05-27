package com.weather.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WeatherRequest {
    private String country;
    private String city;
    private String apiKey;
}

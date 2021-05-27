package com.weather.demo.domain.openweather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Weather {
    private String id;
    private String main;
    private String description;
}

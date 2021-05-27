package com.weather.demo.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class WeatherException extends Exception{
    private String name;
    private String value;

    public WeatherException(String msg){
        super(msg);
    }
}

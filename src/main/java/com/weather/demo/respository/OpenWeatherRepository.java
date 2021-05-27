package com.weather.demo.respository;

import com.weather.demo.domain.openweather.OpenWeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OpenWeatherRepository {
    Logger logger = LoggerFactory.getLogger(OpenWeatherRepository.class);

    private final RestTemplate restTemplate;

    @Autowired
    private Environment environment;


    public OpenWeatherRepository(RestTemplateBuilder restTemplateBuilder){
        restTemplate = restTemplateBuilder.build();
    }

    public OpenWeatherResponse getOpenWeather(String country, String city){

        String baseUrl = environment.getProperty("openweather.url");
        logger.info("openWeather base url: " + baseUrl);

        WebClient webclient = WebClient.builder()
                                .baseUrl(baseUrl)
                                .build();


        String openWeatherpath = environment.getProperty("openweather.path");
        String openWeatherAppId = environment.getProperty("openweather.appid");
        String path = openWeatherpath + "?q=" + city + "," + country + "&APPID=" + openWeatherAppId;
        logger.info("openWeather url path: " + path);

        logger.info("webclient calling openweather...");
        return webclient
                .get()
                .uri(path)
                .retrieve()
                .toEntity(OpenWeatherResponse.class)
                .block()
                .getBody();

    }

    //not in use as restTemplate is older version.
    public OpenWeatherResponse getOpenWeatherwithRestTemplate(String country, String city){

        String url = "http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=7ee313864d5f9aac50536cbaf3d097c0";

        StringBuilder sb = new StringBuilder("http://api.openweathermap.org/data/2.5/weather?q=");
        sb.append(city);
        sb.append(",");
        sb.append(country);
        sb.append("&APPID=");
        sb.append("7ee313864d5f9aac50536cbaf3d097c0");
        url =sb.toString();

        logger.info("calling Open weather service: " + url);
        OpenWeatherResponse res = restTemplate.getForObject(url, OpenWeatherResponse.class);

        return res;

    }

}

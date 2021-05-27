package com.weather.demo.service;

import com.weather.demo.domain.WeatherResponse;
import com.weather.demo.domain.entity.WeatherDTO;
import com.weather.demo.domain.entity.WeatherId;
import com.weather.demo.domain.openweather.OpenWeatherResponse;
import com.weather.demo.domain.openweather.Weather;
import com.weather.demo.respository.OpenWeatherRepository;
import com.weather.demo.respository.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;

@Service
public class WeatherForecastService {

    Logger logger = LoggerFactory.getLogger(WeatherForecastService.class);

    @Autowired
    private OpenWeatherRepository openWeatherRepository;

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private Environment environment;

    public ResponseEntity<WeatherResponse> getWeather(String country, String city){

        WeatherResponse response = null;

        Integer cacheExpiryHour = Integer.parseInt(environment.getProperty("weather.cache.hours"));
        logger.info("cache expiry hour: " + cacheExpiryHour);

        Optional<WeatherDTO> o =
                weatherRepository.findById(WeatherId.builder().country(country).city(city).build());

        if(o.isPresent()){
            WeatherDTO weatherDTO = o.get();
            Date lastUpdated = weatherDTO.getLastUpdated();
            Date currentDate = new Date();

            long difference = currentDate.getTime() - lastUpdated.getTime();

            long difference_In_Hours = difference / (1000 * 60 * 60);

            if(difference_In_Hours < cacheExpiryHour){ // return properties, default is 2 hours
                logger.info("retrieve weather information from DB for country: " + country + ", city: " + city) ;
                response = WeatherResponse.builder()
                        .status("Success")
                        .weatherDescription(weatherDTO.getWeather())
                        .build();
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }
        }

        //return from Open weather service if not existing in DB or ourdated
        logger.info("retrieve weather information from Open Weather service for country: " + country + ", city: " + city) ;
        OpenWeatherResponse res= openWeatherRepository.getOpenWeather(country, city);


        if(res.getWeather()!= null && res.getWeather().size()>0){
            Weather w = res.getWeather().get(0);

            //save it to DB for cache
            if(o.isPresent()){//update to the current weather record
                WeatherDTO weatherDTO = o.get();
                weatherDTO.setWeather(w.getDescription());
                weatherDTO.setLastUpdated(new Date());
                weatherRepository.save(weatherDTO);
            }else{
                //insert a new weather record
                WeatherDTO weatherDTO = WeatherDTO.builder()
                        .weather(w.getDescription())
                        .lastUpdated(new Date())
                        .weatherId(WeatherId.builder()
                                .country(country)
                                .city(city)
                                .build())
                        .build();
                weatherRepository.save(weatherDTO);
            }

            //return the weather
            response = WeatherResponse.builder()
                    .status("Success")
                    .weatherDescription(w.getDescription())
                    .build();
        }

        return new ResponseEntity<WeatherResponse>(response, HttpStatus.OK);
    }

}

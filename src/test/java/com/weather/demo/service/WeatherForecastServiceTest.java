package com.weather.demo.service;

import com.weather.demo.domain.WeatherResponse;
import com.weather.demo.domain.entity.WeatherDTO;
import com.weather.demo.domain.entity.WeatherId;
import com.weather.demo.domain.openweather.OpenWeatherResponse;
import com.weather.demo.domain.openweather.Weather;
import com.weather.demo.respository.OpenWeatherRepository;
import com.weather.demo.respository.WeatherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WeatherForecastServiceTest {

    @Mock
    private OpenWeatherRepository openWeatherRepository;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private Environment environment;

    @InjectMocks
    private WeatherForecastService weatherForecastService;

    @Test
    public void getWeather_EmptyDB_shouldReturnfromOpenWeather(){
        //set up mock behaviour
        List<Weather> list = Stream.of(Weather.builder().description("broken cloud").build())
                                    .collect(Collectors.toList());

        WeatherId weatherId = WeatherId.builder().country("au").city("Melbourne").build();

        Mockito.when(environment.getProperty("weather.cache.hours"))
                .thenReturn("2");

        Mockito.when(openWeatherRepository.getOpenWeather("au", "Melbourne")).
                thenReturn(OpenWeatherResponse.builder().weather(list).build());

        Mockito.when(weatherRepository.findById(weatherId))
                .thenReturn(Optional.empty());

        //call service
        ResponseEntity<WeatherResponse> responseEntity = weatherForecastService.getWeather("au", "Melbourne");


        //assert service response
        assertAll("responseEntity",
                () -> {
                    assertNotNull(responseEntity);
                    assertAll("body",
                            () -> assertNotNull(responseEntity.getBody().getWeatherDescription()),
                            () -> assertEquals("broken cloud", responseEntity.getBody().getWeatherDescription())
                            );
                }
                );

    }

    @Test
    public void getWeather_WeatherDBWithin2Hours_shouldReturnWeatherFromDB(){
        //set up mock behaviour
        List<Weather> list = Stream.of(Weather.builder().description("broken cloud").build())
                .collect(Collectors.toList());

        WeatherId weatherId = WeatherId.builder().country("au").city("Melbourne").build();

        Mockito.when(environment.getProperty("weather.cache.hours"))
                .thenReturn("2");

        Mockito.when(openWeatherRepository.getOpenWeather("au", "Melbourne")).
                thenReturn(OpenWeatherResponse.builder().weather(list).build());

        Optional<WeatherDTO> o = Optional.of(WeatherDTO.builder()
                .weather("clear sky")
                .lastUpdated(new Date()).build());

        assertTrue(o.isPresent());

        Mockito.when(weatherRepository.findById(any()))
                .thenReturn(o);

        //call service
        ResponseEntity<WeatherResponse> responseEntity = weatherForecastService.getWeather("au", "Melbourne");

        //assert service response
        assertAll("responseEntity",
                () -> {
                    assertNotNull(responseEntity);
                    assertAll("body",
                            () -> assertNotNull(responseEntity.getBody().getWeatherDescription()),
                            () -> assertEquals("clear sky", responseEntity.getBody().getWeatherDescription())
                    );
                }
        );

    }

    @Test
    public void getWeather_WeatherDBOver2Hours_shouldReturnWeatherFromOpenWeather() throws Exception{
        //set up mock behaviour

        //test data from Open Weather
        List<Weather> list = Stream.of(Weather.builder().description("broken cloud").build())
                .collect(Collectors.toList());

        Mockito.when(environment.getProperty("weather.cache.hours"))
                .thenReturn("2");

        Mockito.when(openWeatherRepository.getOpenWeather("au", "Melbourne")).
                thenReturn(OpenWeatherResponse.builder().weather(list).build());

        //test data from Weather DB
        SimpleDateFormat  sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date d = sdf.parse("21/05/2021");

        sdf.getCalendar();
        Optional<WeatherDTO> o = Optional.of(WeatherDTO.builder()
                .weather("clear sky")
                .lastUpdated(d)
                .build());

        assertTrue(o.isPresent());

        Mockito.when(weatherRepository.findById(any()))
                .thenReturn(o);

        //call service
        ResponseEntity<WeatherResponse> responseEntity = weatherForecastService.getWeather("au", "Melbourne");

        //assert service response
        assertAll("responseEntity",
                () -> {
                    assertNotNull(responseEntity);
                    assertAll("body",
                            () -> assertNotNull(responseEntity.getBody().getWeatherDescription()),
                            () -> assertEquals("broken cloud", responseEntity.getBody().getWeatherDescription())
                    );
                }
        );

    }

}

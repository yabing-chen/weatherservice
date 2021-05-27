package com.weather.demo.controller;

import com.weather.demo.domain.WeatherResponse;
import com.weather.demo.exception.WeatherException;
import com.weather.demo.respository.OpenWeatherRepository;
import com.weather.demo.respository.WeatherRepository;
import com.weather.demo.service.APIKeyService;
import com.weather.demo.service.WeatherForecastService;
import com.weather.demo.service.WeatherValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@MockitoSettings(strictness = Strictness.LENIENT)
public class WeatherForcastControllerTest {

    private MockMvc mvc;

    @InjectMocks
    private WeatherForecastController weatherForecastController;

    @Mock
    private WeatherForecastService service;

    @Mock
    private WeatherValidatorService weatherValidatorService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(weatherForecastController).build();
    }

    @Test
    public void givenWeather_whenGetWeather_thenReturnAsyncResult() throws Exception{
        WeatherResponse weatherResponse = WeatherResponse.builder()
                                .status("Success").weatherDescription("clear sky").build();

        ResponseEntity res = ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(weatherResponse);

        given(service.getWeather("au", "Melbourne")).willReturn(res);

        given(weatherValidatorService.validateRequest(any(), any())).willReturn(true);

        MvcResult mvcResult  = mvc.perform(get("/api/v1/weather/au/Melbourne")
                    .header("x-api-key", "test"))
                    .andExpect(request().asyncStarted())
                    .andDo(MockMvcResultHandlers.log())
                    .andReturn();


        mvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().json("{\"weatherDescription\":\"clear sky\",\"status\":\"Success\"}", true));

    }

    @Test
    public void givenWeather_whenGetWeatherInvalidRequest_thenReturnAsyncError() throws Exception{
        WeatherResponse weatherResponse = WeatherResponse.builder()
                .status("failed").build();

        ResponseEntity res = ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(weatherResponse);

        given(service.getWeather("au", "Melbourne")).willReturn(res);

        given(weatherValidatorService
                .validateRequest(any(), any()))
                .willReturn(true);

        MvcResult mvcResult  = mvc.perform(get("/api/v1/weather/au/Melbourne")
                .header("x-api-key", "test"))
                .andExpect(request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();


        mvc.perform(asyncDispatch(mvcResult)).andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().json("{\"status\":\"failed\"}", true));


    }

}

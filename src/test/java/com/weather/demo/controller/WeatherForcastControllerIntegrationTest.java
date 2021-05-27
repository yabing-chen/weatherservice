package com.weather.demo.controller;

import com.weather.demo.Application;
import com.weather.demo.domain.WeatherResponse;
import com.weather.demo.respository.OpenWeatherRepository;
import com.weather.demo.respository.WeatherRepository;
import com.weather.demo.service.APIKeyService;
import com.weather.demo.service.WeatherForecastService;
import com.weather.demo.service.WeatherValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherForcastControllerIntegrationTest {
    @LocalServerPort
    private int port;

    private WebTestClient webclient;

    @Test
    public void getRequest_missingAPIKey_returnError() throws Exception {
        webclient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        webclient.get().uri("/api/v1/weather/au/Melbourne")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody().jsonPath("status").isEqualTo("failed")
                .jsonPath("error").isEqualTo("Missing header x-api-key");

    }


    @Test
    public void getRequest_invalidAPIKey_returnError() throws Exception {
        webclient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        webclient.get().uri("/api/v1/weather/au/Melbourne")
                .header("x-api-key", "test")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody().jsonPath("status").isEqualTo("failed")
                                .jsonPath("error").isEqualTo("Invalid header x-api-key");

    }


    @Test
    public void getRequest_validAPIKey_returnWeather() throws Exception {
        webclient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        webclient.get().uri("/api/v1/weather/au/Melbourne")
                .header("x-api-key", "2492a246-d9d2-4f2b-8c8d-b2ee9a87c129")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().jsonPath("status").isEqualTo("Success")
                .jsonPath("weatherDescription").isNotEmpty();

    }

}
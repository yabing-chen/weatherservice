package com.weather.demo.service;

import com.weather.demo.domain.entity.APIKey;
import com.weather.demo.exception.WeatherException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WeatherValidatorServiceTest {

    @Mock
    private APIKeyService apiKeyService;



    @InjectMocks
    private WeatherValidatorService weatherValidatorService;

    private Map<String, String> headers;
    private HttpServletResponse response;

    @BeforeEach
    public void setup(){
        headers = new HashMap<String, String>();
        response = new HttpServletResponse() {
            @Override
            public void addCookie(Cookie cookie) {

            }

            @Override
            public boolean containsHeader(String name) {
                return false;
            }

            @Override
            public String encodeURL(String url) {
                return null;
            }

            @Override
            public String encodeRedirectURL(String url) {
                return null;
            }

            @Override
            public String encodeUrl(String url) {
                return null;
            }

            @Override
            public String encodeRedirectUrl(String url) {
                return null;
            }

            @Override
            public void sendError(int sc, String msg) throws IOException {

            }

            @Override
            public void sendError(int sc) throws IOException {

            }

            @Override
            public void sendRedirect(String location) throws IOException {

            }

            @Override
            public void setDateHeader(String name, long date) {

            }

            @Override
            public void addDateHeader(String name, long date) {

            }

            @Override
            public void setHeader(String name, String value) {

            }

            @Override
            public void addHeader(String name, String value) {

            }

            @Override
            public void setIntHeader(String name, int value) {

            }

            @Override
            public void addIntHeader(String name, int value) {

            }

            @Override
            public void setStatus(int sc) {

            }

            @Override
            public void setStatus(int sc, String sm) {

            }

            @Override
            public int getStatus() {
                return 0;
            }

            @Override
            public String getHeader(String name) {
                return null;
            }

            @Override
            public Collection<String> getHeaders(String name) {
                return null;
            }

            @Override
            public Collection<String> getHeaderNames() {
                return null;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            public PrintWriter getWriter() throws IOException {
                return null;
            }

            @Override
            public void setCharacterEncoding(String charset) {

            }

            @Override
            public void setContentLength(int len) {

            }

            @Override
            public void setContentLengthLong(long length) {

            }

            @Override
            public void setContentType(String type) {

            }

            @Override
            public void setBufferSize(int size) {

            }

            @Override
            public int getBufferSize() {
                return 0;
            }

            @Override
            public void flushBuffer() throws IOException {

            }

            @Override
            public void resetBuffer() {

            }

            @Override
            public boolean isCommitted() {
                return false;
            }

            @Override
            public void reset() {

            }

            @Override
            public void setLocale(Locale loc) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }
        };

    }

    @Test
    public void validateRequest_missingAPIKey_shouldThrowsError(){
        Assertions.assertThrows(WeatherException.class, () ->{
            weatherValidatorService.validateRequest(headers, response);
        });
    }

    @Test
    public void validateRequest_invalidAPIKey_shouldThrowsError(){
        Mockito.when(apiKeyService.getAPIKeyById("invalidkey")).thenReturn(null);

        headers.put("x-api-key", "invalidkey");
        Assertions.assertThrows(WeatherException.class, () ->{
            weatherValidatorService.validateRequest(headers, response);
        });
    }

    @Test
    public void validateRequest_validAPIKey_shouldReturnTrue() throws Exception{

        Bucket bucket = Bucket4j.builder()
                .addLimit(Bandwidth.classic(5, Refill.of(5, Duration.ofMinutes(1))))
                .build();

        Mockito.when(apiKeyService.getAPIKeyById("35959dcc-b67e-4107-bee3-c6a4db60fb45"))
                .thenReturn(APIKey.builder().id("35959dcc-b67e-4107-bee3-c6a4db60fb45").build());

        Mockito.when((apiKeyService.getBucket(any())))
                    .thenReturn(bucket);

        headers.put("x-api-key", "35959dcc-b67e-4107-bee3-c6a4db60fb45");

        assertTrue(weatherValidatorService.validateRequest(headers, response));

    }

    @Test
    public void validateRequest_validAPIKeyTrigger5timesWithin1min_shouldReturnTrue() throws Exception{

        Bucket bucket = Bucket4j.builder()
                .addLimit(Bandwidth.classic(5, Refill.of(5, Duration.ofMinutes(1))))
                .build();

        Mockito.when(apiKeyService.getAPIKeyById("35959dcc-b67e-4107-bee3-c6a4db60fb45"))
                .thenReturn(APIKey.builder().id("35959dcc-b67e-4107-bee3-c6a4db60fb45").build());

        Mockito.when((apiKeyService.getBucket(any())))
                .thenReturn(bucket);

        headers.put("x-api-key", "35959dcc-b67e-4107-bee3-c6a4db60fb45");

        //calling the service 5 times within 1 minutes
        assertTrue(weatherValidatorService.validateRequest(headers, response));
        assertTrue(weatherValidatorService.validateRequest(headers, response));
        assertTrue(weatherValidatorService.validateRequest(headers, response));
        assertTrue(weatherValidatorService.validateRequest(headers, response));
        assertTrue(weatherValidatorService.validateRequest(headers, response));

    }

    @Test
    public void validateRequest_validAPIKeyTriggerMorethan5timesWithin1min_shouldThrowsError() throws Exception{

        Bucket bucket = Bucket4j.builder()
                .addLimit(Bandwidth.classic(5, Refill.of(5, Duration.ofMinutes(1))))
                .build();

        Mockito.when(apiKeyService.getAPIKeyById("35959dcc-b67e-4107-bee3-c6a4db60fb45"))
                .thenReturn(APIKey.builder().id("35959dcc-b67e-4107-bee3-c6a4db60fb45").build());

        Mockito.when((apiKeyService.getBucket(any())))
                .thenReturn(bucket);

        headers.put("x-api-key", "35959dcc-b67e-4107-bee3-c6a4db60fb45");

        assertTrue(weatherValidatorService.validateRequest(headers, response));
        assertTrue(weatherValidatorService.validateRequest(headers, response));
        assertTrue(weatherValidatorService.validateRequest(headers, response));
        assertTrue(weatherValidatorService.validateRequest(headers, response));
        assertTrue(weatherValidatorService.validateRequest(headers, response));

        //the next time will throw error
        Assertions.assertThrows(WeatherException.class, () ->{
            weatherValidatorService.validateRequest(headers, response);
        });

    }

}

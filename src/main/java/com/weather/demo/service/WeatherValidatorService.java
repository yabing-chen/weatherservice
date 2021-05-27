package com.weather.demo.service;

import com.weather.demo.domain.WeatherResponse;
import com.weather.demo.domain.entity.APIKey;
import com.weather.demo.exception.WeatherException;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WeatherValidatorService {

    Logger logger = LoggerFactory.getLogger(WeatherValidatorService.class);

    @Autowired
    private APIKeyService apiKeyService;

    private boolean validateKey(String apiKey){
        boolean result = false;

        APIKey key = apiKeyService.getAPIKeyById(apiKey);

        return key != null? true:false;

    }

    public boolean validateRequest(Map<String, String> headers, HttpServletResponse response) throws WeatherException {
        logger.info("validate x-api-key");

        String apiKey = headers.entrySet().stream()
                .filter(map -> "x-api-key".equals(map.getKey()))
                .map(map -> map.getValue())
                .collect(Collectors.joining());

        if(apiKey == null || apiKey.isEmpty()){
            logger.info("missing header x-api-key");
            throw new WeatherException("Missing header x-api-key");
        }

        if (!validateKey(apiKey)){
            logger.info("invalid header x-api-key");
            throw new WeatherException("Invalid header x-api-key");
        }

        Bucket tokenBucket = apiKeyService.getBucket(apiKey);
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            logger.info("too many requests recieved");
            throw new WeatherException("Too many rquests for the API key");
        }else{
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
        }

        return true;
    }
}

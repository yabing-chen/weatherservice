package com.weather.demo.service;

import com.weather.demo.domain.entity.APIKey;
import com.weather.demo.respository.APIKeyRepository;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class APIKeyService {

    @Autowired
    private APIKeyRepository apiKeyRepository;

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public List getAllAPIKeys(){
        List<APIKey> apiKeys = new ArrayList<APIKey>();
        apiKeyRepository.findAll().forEach(apiKey -> apiKeys.add(apiKey));
        return apiKeys;
    }

    public APIKey getAPIKeyById(String apiKey){
        Optional<APIKey> key = apiKeyRepository.findById(apiKey);
        return key.isPresent()?key.get():null;

    }

    public Bucket getBucket(String apiKey){
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String apiKey){
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(5, Refill.of(5, Duration.ofHours(1))))
                .build();
    }

}

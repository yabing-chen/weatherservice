package com.weather.demo.respository;

import com.weather.demo.domain.entity.APIKey;
import org.springframework.data.repository.CrudRepository;

public interface APIKeyRepository extends CrudRepository<APIKey, String> {
}

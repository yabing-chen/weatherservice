package com.weather.demo.respository;

import com.weather.demo.domain.entity.WeatherDTO;
import com.weather.demo.domain.entity.WeatherId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface WeatherRepository extends CrudRepository<WeatherDTO, WeatherId> {


}

package com.weather.demo.domain.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherId implements Serializable {

    private String country;
    private String city;

}

package com.weather.demo.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name="Weather")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherDTO {

    @EmbeddedId
    private WeatherId weatherId;

    @Column
    private String weather;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
}

package com.example.temperaturemanagement.temperature;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemperatureStats {

    private Double minTemperature;
    private Double maxTemperature;
    private Double averageTemperature;
    private Long totalReadings;
    private Map<LocalTime, Double> averageByHour;
    private Map<Integer, Double> averageByDayOfWeek;
}
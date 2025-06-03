package com.example.temperaturemanagement.temperature;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemperatureCreateRequest {

    @NotNull(message = "Data jest wymagana")
    private LocalDate date;

    @Min(value = 1, message = "Dzień tygodnia musi być od 1 do 7")
    @Max(value = 7, message = "Dzień tygodnia musi być od 1 do 7")
    private Integer dayOfWeek;

    @NotNull(message = "Godzina jest wymagana")
    private LocalTime hour;

    @NotNull(message = "Temperatura jest wymagana")
    @DecimalMin(value = "-100.0", message = "Temperatura nie może być niższa niż -100°C")
    @DecimalMax(value = "100.0", message = "Temperatura nie może być wyższa niż 100°C")
    private Double temperature;

    @Size(max = 50, message = "Źródło nie może być dłuższe niż 50 znaków")
    private String source;
}
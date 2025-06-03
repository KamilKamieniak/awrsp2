package com.example.temperaturemanagement.temperature;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "temperature_readings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemperatureReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data jest wymagana")
    @Column(nullable = false)
    private LocalDate date;

    @Min(value = 1, message = "Dzień tygodnia musi być od 1 do 7")
    @Max(value = 7, message = "Dzień tygodnia musi być od 1 do 7")
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @NotNull(message = "Godzina jest wymagana")
    @Column(name = "reading_hour", nullable = false)  // ← POPRAWKA!
    private LocalTime hour;

    @NotNull(message = "Temperatura jest wymagana")
    @DecimalMin(value = "-100.0", message = "Temperatura nie może być niższa niż -100°C")
    @DecimalMax(value = "100.0", message = "Temperatura nie może być wyższa niż 100°C")
    @Column(nullable = false)
    private Double temperature;

    @Size(max = 50, message = "Źródło nie może być dłuższe niż 50 znaków")
    @Column(length = 50)
    private String source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
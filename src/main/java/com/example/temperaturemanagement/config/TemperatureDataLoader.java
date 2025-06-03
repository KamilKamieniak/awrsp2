package com.example.temperaturemanagement.config;

import com.example.temperaturemanagement.temperature.TemperatureReading;
import com.example.temperaturemanagement.temperature.TemperatureReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TemperatureDataLoader implements CommandLineRunner {

    private final TemperatureReadingRepository temperatureRepository;

    @Override
    public void run(String... args) throws Exception {
        if (temperatureRepository.count() == 0) {
            log.info("Baza danych jest pusta. Ładowanie przykładowych danych temperatury...");
            loadSampleTemperatureData();
            log.info("Przykładowe dane temperatury załadowane pomyślnie!");
        } else {
            log.info("Baza danych zawiera już {} odczytów. Pomijanie ładowania przykładowych danych.",
                    temperatureRepository.count());
        }
    }

    private void loadSampleTemperatureData() {
        List<TemperatureReading> sampleReadings = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int day = 0; day < 7; day++) {
            LocalDate date = today.minusDays(day);
            int dayOfWeek = date.getDayOfWeek().getValue();

            for (int hour = 0; hour < 24; hour += 2) {
                LocalTime time = LocalTime.of(hour, 0);

                double baseTemp = 15.0;
                double hourlyVariation = Math.sin((hour - 6) * Math.PI / 12) * 8; // -8°C do +8°C
                double randomVariation = (Math.random() - 0.5) * 4;
                double seasonalVariation = Math.sin((today.getDayOfYear() - 80) * 2 * Math.PI / 365) * 10;
                double temperature = baseTemp + hourlyVariation + randomVariation + seasonalVariation;

                temperature = Math.round(temperature * 10.0) / 10.0;

                TemperatureReading reading = TemperatureReading.builder()
                        .date(date)
                        .dayOfWeek(dayOfWeek)
                        .hour(time)
                        .temperature(temperature)
                        .source("SAMPLE_DATA")
                        .build();

                sampleReadings.add(reading);
            }
        }

        sampleReadings.add(TemperatureReading.builder()
                .date(today)
                .dayOfWeek(today.getDayOfWeek().getValue())
                .hour(LocalTime.of(12, 0))
                .temperature(22.5)
                .source("WEATHER_API_WARSAW")
                .build());

        sampleReadings.add(TemperatureReading.builder()
                .date(today.minusDays(1))
                .dayOfWeek(today.minusDays(1).getDayOfWeek().getValue())
                .hour(LocalTime.of(15, 0))
                .temperature(18.3)
                .source("CSV")
                .build());

        temperatureRepository.saveAll(sampleReadings);
        log.info("Załadowano {} przykładowych odczytów temperatury", sampleReadings.size());
    }
}
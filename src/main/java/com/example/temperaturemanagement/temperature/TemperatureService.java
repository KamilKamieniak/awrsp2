package com.example.temperaturemanagement.temperature;

import com.example.temperaturemanagement.exception.InvalidTemperatureDataException;
import com.example.temperaturemanagement.exception.TemperatureNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TemperatureService {

    private final TemperatureReadingRepository temperatureRepository;

    public TemperatureReading createReading(TemperatureCreateRequest request) {
        log.info("Tworzenie nowego odczytu temperatury dla daty: {}", request.getDate());

        TemperatureReading reading = TemperatureReading.builder()
                .date(request.getDate())
                .dayOfWeek(request.getDayOfWeek())
                .hour(request.getHour())
                .temperature(request.getTemperature())
                .source(request.getSource() != null ? request.getSource() : "MANUAL")
                .build();

        TemperatureReading saved = temperatureRepository.save(reading);
        log.info("Odczyt temperatury utworzony z ID: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<TemperatureReading> getAllReadings() {
        log.info("Pobieranie wszystkich odczytów temperatury");
        return temperatureRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TemperatureReading getReadingById(Long id) {
        log.info("Pobieranie odczytu temperatury o ID: {}", id);
        return temperatureRepository.findById(id)
                .orElseThrow(() -> new TemperatureNotFoundException("Nie znaleziono odczytu o ID: " + id));
    }

    public TemperatureReading updateReading(Long id, TemperatureUpdateRequest request) {
        log.info("Aktualizacja odczytu temperatury o ID: {}", id);

        TemperatureReading reading = getReadingById(id);

        if (request.getDate() != null) {
            reading.setDate(request.getDate());
        }
        if (request.getDayOfWeek() != null) {
            reading.setDayOfWeek(request.getDayOfWeek());
        }
        if (request.getHour() != null) {
            reading.setHour(request.getHour());
        }
        if (request.getTemperature() != null) {
            reading.setTemperature(request.getTemperature());
        }
        if (request.getSource() != null) {
            reading.setSource(request.getSource());
        }

        TemperatureReading updated = temperatureRepository.save(reading);
        log.info("Odczyt temperatury zaktualizowany o ID: {}", id);
        return updated;
    }

    public void deleteReading(Long id) {
        log.info("Usuwanie odczytu temperatury o ID: {}", id);
        TemperatureReading reading = getReadingById(id);
        temperatureRepository.delete(reading);
        log.info("Odczyt temperatury usunięty o ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<TemperatureReading> getReadingsByDate(LocalDate date) {
        log.info("Pobieranie odczytów dla daty: {}", date);
        return temperatureRepository.findByDateOrderByHour(date);
    }

    @Transactional(readOnly = true)
    public List<TemperatureReading> getReadingsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Pobieranie odczytów dla zakresu dat: {} - {}", startDate, endDate);
        return temperatureRepository.findByDateBetween(startDate, endDate);
    }

    public List<TemperatureReading> importFromCsv(MultipartFile file) throws IOException {
        log.info("Import danych z pliku CSV: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new InvalidTemperatureDataException("Plik CSV jest pusty");
        }

        String content = new String(file.getBytes());
        String[] lines = content.split("\n");

        if (lines.length <= 1) {
            throw new InvalidTemperatureDataException("Plik CSV nie zawiera danych");
        }

        List<TemperatureReading> readings = new ArrayList<>();

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            try {
                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                LocalDate date = LocalDate.parse(parts[0].trim());
                Integer dayOfWeek = Integer.parseInt(parts[1].trim());
                LocalTime hour = LocalTime.parse(parts[2].trim());
                Double temperature = Double.parseDouble(parts[3].trim());

                TemperatureReading reading = TemperatureReading.builder()
                        .date(date)
                        .dayOfWeek(dayOfWeek)
                        .hour(hour)
                        .temperature(temperature)
                        .source("CSV")
                        .build();

                readings.add(reading);

            } catch (Exception e) {
                log.error("Błąd parsowania linii {} w CSV: {}", i + 1, e.getMessage());
                throw new InvalidTemperatureDataException("Błąd w linii " + (i + 1) + ": " + e.getMessage());
            }
        }

        if (readings.isEmpty()) {
            throw new InvalidTemperatureDataException("Nie znaleziono prawidłowych danych w pliku CSV");
        }

        List<TemperatureReading> saved = temperatureRepository.saveAll(readings);
        log.info("Zaimportowano {} odczytów z pliku CSV", saved.size());
        return saved;
    }

    @Transactional(readOnly = true)
    public TemperatureStats getStatistics() {
        log.info("Pobieranie statystyk temperatury");

        Object[] stats = temperatureRepository.getTemperatureStatistics();
        Long totalCount = temperatureRepository.count();
        Map<LocalTime, Double> hourlyAverages = getAverageTemperatureByHour();

        double min = 0.0;
        double max = 0.0;
        double avg = 0.0;

        if (stats != null && stats.length == 3) {
            if (stats[0] instanceof Number) min = ((Number) stats[0]).doubleValue();
            if (stats[1] instanceof Number) max = ((Number) stats[1]).doubleValue();
            if (stats[2] instanceof Number) avg = ((Number) stats[2]).doubleValue();
        }

        return TemperatureStats.builder()
                .minTemperature(min)
                .maxTemperature(max)
                .averageTemperature(avg)
                .totalReadings(totalCount)
                .averageByHour(hourlyAverages)
                .averageByDayOfWeek(null)
                .build();
    }

    @Transactional(readOnly = true)
    public Map<LocalTime, Double> getAverageTemperatureByHour() {
        log.info("Pobieranie średniej temperatury według godzin");

        List<Object[]> results = temperatureRepository.getAverageTemperatureByHour();
        Map<LocalTime, Double> averages = new LinkedHashMap<>();

        for (Object[] result : results) {
            LocalTime hour = (LocalTime) result[0];
            Double avgTemp = (Double) result[1];
            averages.put(hour, Math.round(avgTemp * 100.0) / 100.0);
        }

        return averages;
    }

    @Transactional(readOnly = true)
    public List<TemperatureReading> getLatestReadings() {
        log.info("Pobieranie najnowszych odczytów");
        return temperatureRepository.findLatestReadings();
    }

    @Transactional(readOnly = true)
    public Double getAverageTemperatureByDate(LocalDate date) {
        log.info("Pobieranie średniej temperatury dla daty: {}", date);
        Double average = temperatureRepository.getAverageTemperatureByDate(date);
        return average != null ? Math.round(average * 100.0) / 100.0 : null;
    }
}

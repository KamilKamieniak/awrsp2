package com.example.temperaturemanagement.temperature;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/temperatures")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TemperatureController {

    private final TemperatureService temperatureService;

    @GetMapping
    public ResponseEntity<List<TemperatureReading>> getAllReadings() {
        List<TemperatureReading> readings = temperatureService.getAllReadings();
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemperatureReading> getReadingById(@PathVariable Long id) {
        TemperatureReading reading = temperatureService.getReadingById(id);
        return ResponseEntity.ok(reading);
    }

    @PostMapping
    public ResponseEntity<TemperatureReading> createReading(@Valid @RequestBody TemperatureCreateRequest request) {
        TemperatureReading created = temperatureService.createReading(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemperatureReading> updateReading(
            @PathVariable Long id,
            @Valid @RequestBody TemperatureUpdateRequest request) {
        TemperatureReading updated = temperatureService.updateReading(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReading(@PathVariable Long id) {
        temperatureService.deleteReading(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<TemperatureReading>> getReadingsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TemperatureReading> readings = temperatureService.getReadingsByDate(date);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/range")
    public ResponseEntity<List<TemperatureReading>> getReadingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TemperatureReading> readings = temperatureService.getReadingsByDateRange(startDate, endDate);
        return ResponseEntity.ok(readings);
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<List<TemperatureReading>> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            List<TemperatureReading> imported = temperatureService.importFromCsv(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(imported);
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas czytania pliku: " + e.getMessage(), e);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<TemperatureStats> getStatistics() {
        TemperatureStats stats = temperatureService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/hourly")
    public ResponseEntity<Map<LocalTime, Double>> getHourlyAverages() {
        Map<LocalTime, Double> averages = temperatureService.getAverageTemperatureByHour();
        return ResponseEntity.ok(averages);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<TemperatureReading>> getLatestReadings() {
        List<TemperatureReading> latest = temperatureService.getLatestReadings();
        return ResponseEntity.ok(latest);
    }

    @GetMapping("/average/{date}")
    public ResponseEntity<Double> getAverageTemperatureByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Double average = temperatureService.getAverageTemperatureByDate(date);
        if (average != null) {
            return ResponseEntity.ok(average);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
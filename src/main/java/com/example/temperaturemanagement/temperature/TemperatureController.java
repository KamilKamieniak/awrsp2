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

    // GET /api/temperatures - Pobierz wszystkie odczyty
    @GetMapping
    public ResponseEntity<List<TemperatureReading>> getAllReadings() {
        List<TemperatureReading> readings = temperatureService.getAllReadings();
        return ResponseEntity.ok(readings);
    }

    // GET /api/temperatures/{id} - Pobierz konkretny odczyt
    @GetMapping("/{id}")
    public ResponseEntity<TemperatureReading> getReadingById(@PathVariable Long id) {
        TemperatureReading reading = temperatureService.getReadingById(id);
        return ResponseEntity.ok(reading);
    }

    // POST /api/temperatures - Dodaj nowy odczyt
    @PostMapping
    public ResponseEntity<TemperatureReading> createReading(@Valid @RequestBody TemperatureCreateRequest request) {
        TemperatureReading created = temperatureService.createReading(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/temperatures/{id} - Aktualizuj odczyt
    @PutMapping("/{id}")
    public ResponseEntity<TemperatureReading> updateReading(
            @PathVariable Long id,
            @Valid @RequestBody TemperatureUpdateRequest request) {
        TemperatureReading updated = temperatureService.updateReading(id, request);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/temperatures/{id} - Usuń odczyt
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReading(@PathVariable Long id) {
        temperatureService.deleteReading(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/temperatures/date/{date} - Odczyty dla konkretnej daty
    @GetMapping("/date/{date}")
    public ResponseEntity<List<TemperatureReading>> getReadingsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TemperatureReading> readings = temperatureService.getReadingsByDate(date);
        return ResponseEntity.ok(readings);
    }

    // GET /api/temperatures/range - Odczyty w zakresie dat
    @GetMapping("/range")
    public ResponseEntity<List<TemperatureReading>> getReadingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TemperatureReading> readings = temperatureService.getReadingsByDateRange(startDate, endDate);
        return ResponseEntity.ok(readings);
    }

    // POST /api/temperatures/upload-csv - Upload pliku CSV
    @PostMapping("/upload-csv")
    public ResponseEntity<List<TemperatureReading>> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            List<TemperatureReading> imported = temperatureService.importFromCsv(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(imported);
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas czytania pliku: " + e.getMessage(), e);
        }
    }

    // GET /api/temperatures/stats - Statystyki
    @GetMapping("/stats")
    public ResponseEntity<TemperatureStats> getStatistics() {
        TemperatureStats stats = temperatureService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    // GET /api/temperatures/stats/hourly - Średnie według godzin
    @GetMapping("/stats/hourly")
    public ResponseEntity<Map<LocalTime, Double>> getHourlyAverages() {
        Map<LocalTime, Double> averages = temperatureService.getAverageTemperatureByHour();
        return ResponseEntity.ok(averages);
    }

    // GET /api/temperatures/latest - Najnowsze odczyty
    @GetMapping("/latest")
    public ResponseEntity<List<TemperatureReading>> getLatestReadings() {
        List<TemperatureReading> latest = temperatureService.getLatestReadings();
        return ResponseEntity.ok(latest);
    }

    // GET /api/temperatures/average/{date} - Średnia temperatura dla daty
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
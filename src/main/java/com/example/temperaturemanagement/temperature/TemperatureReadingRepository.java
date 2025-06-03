package com.example.temperaturemanagement.temperature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TemperatureReadingRepository extends JpaRepository<TemperatureReading, Long> {

    // Automatyczne query methods - Spring tworzy SQL z nazw metod
    List<TemperatureReading> findByDate(LocalDate date);

    List<TemperatureReading> findByDayOfWeek(Integer dayOfWeek);

    List<TemperatureReading> findByHour(LocalTime hour);

    List<TemperatureReading> findBySource(String source);

    List<TemperatureReading> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<TemperatureReading> findByTemperatureBetween(Double minTemp, Double maxTemp);

    // Custom queries - dla bardziej złożonych zapytań
    @Query("SELECT t FROM TemperatureReading t WHERE t.date = :date ORDER BY t.hour")
    List<TemperatureReading> findByDateOrderByHour(@Param("date") LocalDate date);

    @Query("SELECT AVG(t.temperature) FROM TemperatureReading t WHERE t.date = :date")
    Double getAverageTemperatureByDate(@Param("date") LocalDate date);

    @Query("SELECT t.hour, AVG(t.temperature) FROM TemperatureReading t GROUP BY t.hour ORDER BY t.hour")
    List<Object[]> getAverageTemperatureByHour();

    @Query("SELECT COUNT(t) FROM TemperatureReading t WHERE t.date = :date")
    Long countReadingsByDate(@Param("date") LocalDate date);

    // Do prognozowania - średnie z poprzednich dni
    @Query("SELECT AVG(t.temperature) FROM TemperatureReading t WHERE t.hour = :hour AND t.dayOfWeek = :dayOfWeek")
    Double getAverageTemperatureByHourAndDayOfWeek(@Param("hour") LocalTime hour, @Param("dayOfWeek") Integer dayOfWeek);

    // Statystyki temperatury
    @Query("SELECT MIN(t.temperature), MAX(t.temperature), AVG(t.temperature) FROM TemperatureReading t")
    Object[] getTemperatureStatistics();

    // Najnowsze odczyty (dla dashboard)
    @Query("SELECT t FROM TemperatureReading t ORDER BY t.createdAt DESC LIMIT 10")
    List<TemperatureReading> findLatestReadings();

    // Ostatni odczyt dla konkretnej daty
    @Query("SELECT t FROM TemperatureReading t WHERE t.date = :date ORDER BY t.hour DESC LIMIT 1")
    TemperatureReading findLatestReadingByDate(@Param("date") LocalDate date);
}
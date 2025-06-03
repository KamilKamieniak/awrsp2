# Temperature Management App

Aplikacja do zarządzania i analizy danych o temperaturze — z możliwością dodawania pomiarów, ich przeglądania, edycji, importu z pliku CSV i generowania statystyk.

## Funkcjonalności

REST API umożliwia:

1. Pobieranie wszystkich pomiarów  
   `GET /api/temperatures`

2. Pobieranie pomiaru po ID  
   `GET /api/temperatures/{id}`

3. Dodawanie nowego pomiaru  
   `POST /api/temperatures`

4. Aktualizacja istniejącego pomiaru  
   `PUT /api/temperatures/{id}`

5. Usuwanie pomiaru  
   `DELETE /api/temperatures/{id}`

6. Pobieranie pomiarów z konkretnej daty  
   `GET /api/temperatures/date/{date}`

7. Pobieranie pomiarów z przedziału dat  
   `GET /api/temperatures/range?startDate=...&endDate=...`

8. Import danych z pliku CSV  
   `POST /api/temperatures/upload-csv`

9. Statystyki ogólne (min, max, średnia)  
   `GET /api/temperatures/stats`

10. Średnia temperatura według godziny  
    `GET /api/temperatures/stats/hourly`

11. Najnowsze pomiary (ostatnie 10)  
    `GET /api/temperatures/latest`

12. Średnia temperatura dla konkretnej daty  
    `GET /api/temperatures/average/{date}`

## Technologie

- Java 22
- Spring Boot 3.5.0
- Spring Data JPA
- H2 Database
- Lombok

## Jak uruchomić

1. Sklonuj repozytorium:

   git clone https://github.com/KamilKamieniak/awrsp2
   cd temperature-management


2. Uruchom aplikację (np. z IntelliJ) lub użyj:


   ./mvnw spring-boot:run


3. Otwórz przeglądarkę i przetestuj np.:

   - `http://localhost:8080/api/temperatures`
   - `http://localhost:8080/h2-console`  
     (JDBC URL: `jdbc:h2:mem:temperaturedb`, user: `sa`, hasło: brak)

## Testowanie API

Rekomendowane narzędzia:
- Postman
- Przeglądarka

## Format pliku CSV (dla importu)

Plik musi mieć rozszerzenie `.csv` i separator `;` (średnik).

Wymagane kolumny:
- `date` – data pomiaru, format: `yyyy-MM-dd` (np. `2025-06-04`)
- `dayOfWeek` – numer dnia tygodnia, 1 (poniedziałek) do 7 (niedziela)
- `hour` – godzina, format: `HH:mm` (np. `08:00`)
- `temperature` – liczba zmiennoprzecinkowa (np. `21.5`)

Przykład zawartości:

date;dayOfWeek;hour;temperature
2025-06-04;3;09:00;18.5
2025-06-04;3;10:00;19.7
2025-06-04;3;11:00;20.3

Aplikacja automatycznie ustawi `source` jako `CSV` i obliczy `createdAt`, `updatedAt`.

## Obsługa błędów

Aplikacja zwraca czytelne komunikaty przy:
- braku wymaganych pól,
- błędnym formacie danych,
- pustym pliku CSV,
- nieistniejącym ID.

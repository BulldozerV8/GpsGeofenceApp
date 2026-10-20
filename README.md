# Geofence Automator

Aplikacja mobilna dla systemu Android do automatyzacji powiadomień i zachowania telefonu (np. wyciszania) na podstawie wirtualnych stref na mapie (Geofencing) oraz harmonogramu czasowego.

## 🚀 Funkcjonalności

* **Zarządzanie strefami:** Rysowanie, edycja i usuwanie obszarów na interaktywnej mapie Google.
* **Logika czasoprzestrzenna:** Reagowanie na strefy tylko w zdefiniowanych godzinach i dniach tygodnia.
* **Automatyzacja profilu:** Samoczynne wyciszanie dzwonka telefonu po wejściu w strefę (np. do pracy lub szkoły).
* **Działanie w tle:** Zoptymalizowane wykrywanie lokalizacji z użyciem *Adaptive Polling* (oszczędzanie baterii).
* **Historia i logi:** Zapis logów wejść i wyjść ze stref do lokalnej bazy danych z możliwością przeglądania.
* **Powiadomienia:** Natywne alerty systemowe informujące o zmianie strefy i statusu telefonu.

## 🛠 Technologie i Architektura

Projekt został zrealizowany w oparciu o wzorzec **MVVM** oraz zasady **Clean Architecture** (separacja warstw: Data, Domain, UI).

* **Język i środowisko:** Kotlin, Android Studio
* **Interfejs (UI):** Jetpack Compose (deklaratywne UI)
* **Baza Danych:** Room Database (lokalna baza SQLite)
* **Usługi Lokalizacyjne:** Google Play Services (Geofencing API, Maps SDK)
* **Logika systemowa:** BroadcastReceiver, Background Services, Notification API

## ⚙️ Instrukcja Instalacji (Środowisko Deweloperskie)

1. **Pobranie projektu:**
   Sklonuj repozytorium na swój dysk lokalny:
   ```bash
   git clone <adres_repozytorium>
2. **Konfiguracja klucza API Map Google:**
   Ze względów bezpieczeństwa klucz API nie jest dołączony do repozytorium.
   - Wygeneruj własny klucz API dla Maps SDK for Android w konsoli Google Cloud.
   - W głównym katalogu projektu utwórz lub wyedytuj plik local.properties i dodaj linię:
   ```bash
   MAPS_API_KEY=twoj_wygenerowany_klucz_api
3. **Uruchomienie:**
   - Otwórz pobrany folder w Android Studio.
   - Poczekaj na zakończenie synchronizacji Gradle.
   - Uruchom aplikację na fizycznym urządzeniu (zalecane) lub na emulatorze.

**Uwaga (Testowanie na emulatorze):**
   Jeśli zdarzenia wejścia w strefę nie są wykrywane, otwórz najpierw systemową aplikację Google Maps na emulatorze, aby "wybudzić" usługi lokalizacyjne urządzenia.

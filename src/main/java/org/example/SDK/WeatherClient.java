package org.example.SDK;

import lombok.RequiredArgsConstructor;
import org.example.DTO.CachedWeatherData;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherClient {
    private final Map<String, CachedWeatherData> cities = new HashMap<>();
    private WeatherService weatherService;
    private final Mode mode;
    private ScheduledExecutorService scheduler;

    public WeatherClient(WeatherService weatherService, Mode mode) {
        this.weatherService = weatherService;
        this.mode = mode;

        if (mode == Mode.POLLING) {
            startPolling();
        }
    }

    private void startPolling() {
        if (scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(this::updateAllCities, 0, 10, TimeUnit.MINUTES);
        }
    }

    public void checkCity(String city) {
        if (cities.containsKey(city)) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime cachedCityTime = cities.get(city).getLastUpdated();
            Duration timeDifference = Duration.between(cachedCityTime, now);
            if (timeDifference.toMinutes() > 10) {
                addCity(city);
            }
        }
        else {
            if (cities.size() < 10) {
                removeOldestCity();
            }
            addCity(city);
        }
    }

    public void updateAllCities() {
        for (Map.Entry<String, CachedWeatherData> entry : cities.entrySet()) {
            entry.setValue(new CachedWeatherData(getWeather(entry.getKey()), LocalDateTime.now()));
        }
    }

    public void removeOldestCity() {
        String oldestCity = null;
        LocalDateTime oldestTime = LocalDateTime.now();

        for (Map.Entry<String, CachedWeatherData> entry : cities.entrySet()) {
            if (entry.getValue().getLastUpdated().isBefore(oldestTime)) {
                oldestTime = entry.getValue().getLastUpdated();
                oldestCity = entry.getKey();
            }
        }

        if (oldestCity != null) {
            cities.remove(oldestCity);
            System.out.println("Oldest city is deleted: " + oldestCity);
        }
    }

    public void addCity(String city) {
        JSONObject weatherData = weatherService.getWeatherData(city);
        cities.put(city, new CachedWeatherData(weatherData, LocalDateTime.now()));
    }

    public JSONObject getWeather(String city) {
        checkCity(city);
        return cities.get(city).getWeatherData();
    }

    public void removeCity(String city) {
        cities.remove(city);
    }

    public Map<String, CachedWeatherData> getCities() {
        return cities;
    }


    public enum Mode {
        ON_DEMAND, POLLING
    }
}

package org.example.DTO;

import lombok.Getter;
import org.json.JSONObject;

import java.time.LocalDateTime;
@Getter
public class CachedWeatherData {
    private JSONObject weatherData;

    private LocalDateTime lastUpdated;

    public CachedWeatherData(JSONObject weatherData, LocalDateTime lastUpdated) {
        this.weatherData = weatherData;
        this.lastUpdated = lastUpdated;
    }

}

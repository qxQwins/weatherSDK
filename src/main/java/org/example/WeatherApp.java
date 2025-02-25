package org.example;

import org.example.DTO.CachedWeatherData;
import org.example.SDK.WeatherClient;
import org.example.SDK.WeatherConfig;
import org.example.SDK.WeatherService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

public class WeatherApp {

    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(WeatherConfig.class);
        WeatherService weatherService = context.getBean(WeatherService.class);
        WeatherClient weatherClient = new WeatherClient(weatherService, WeatherClient.Mode.ON_DEMAND);
        try {
            weatherClient.addCity("Paris");
            weatherClient.addCity("London");
            weatherClient.removeOldestCity();
            for(Map.Entry<String, CachedWeatherData> data : weatherClient.getCities().entrySet()){
                System.out.println(weatherClient.getWeather(data.getKey()).toString(4));
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch weather data: " + e.getMessage());
        }
    }

}

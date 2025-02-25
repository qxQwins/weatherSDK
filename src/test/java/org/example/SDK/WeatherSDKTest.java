package org.example.SDK;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WeatherSDKTest {

    private WeatherClient weatherClient;
    private WeatherSDK weatherSDK;

    @BeforeEach
    void setUp() {
        weatherClient = mock(WeatherClient.class);
        weatherSDK = new WeatherSDK(weatherClient, WeatherClient.Mode.ON_DEMAND);
    }

    @Test
    void getWeather_validCity_returnsWeatherData() {
        JSONObject mockWeatherData = new JSONObject("{\"datetime\":1740487221,\"visibility\":10000," +
                "\"timezone\":0,\"weather\":{\"description\":\"few clouds\",\"main\":\"Clouds\"}," +
                "\"temperature\":{\"temp\":283.64,\"feels_like\":282.57},\"name\":\"London\"," +
                "\"sys\":{\"sunrise\":1740466458,\"sunset\":1740504790},\"wind\":{\"speed\":3.6}}");
        when(weatherClient.getWeather("London")).thenReturn(mockWeatherData);

        JSONObject weatherData = weatherClient.getWeather("London");

        assertNotNull(weatherData);
        assertEquals("London", weatherData.getString("name"));
    }

    @Test
    void getWeather_invalidCity_throwsException() {
        when(weatherClient.getWeather("InvalidCity")).thenThrow(new RuntimeException("City not found"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            weatherClient.getWeather("InvalidCity");
        });

        assertEquals("City not found", exception.getMessage());
    }

    @Test
    void addCity_addsCityToCache() {
        JSONObject weatherData = new JSONObject("{\"datetime\":1740487221,\"visibility\":10000," +
                "\"timezone\":0,\"weather\":{\"description\":\"few clouds\",\"main\":\"Clouds\"}," +
                "\"temperature\":{\"temp\":283.64,\"feels_like\":282.57},\"name\":\"London\"," +
                "\"sys\":{\"sunrise\":1740466458,\"sunset\":1740504790},\"wind\":{\"speed\":3.6}}");
        weatherClient.addCity("London");



        assertEquals(weatherData, weatherClient.getWeather("London"));
    }

    @Test
    void removeOldestCity_removesCityFromCache() {
        JSONObject weatherData1 = new JSONObject("{\"datetime\":1740487221,\"visibility\":10000," +
                "\"timezone\":0,\"weather\":{\"description\":\"few clouds\",\"main\":\"Clouds\"}," +
                "\"temperature\":{\"temp\":283.64,\"feels_like\":282.57},\"name\":\"London\"," +
                "\"sys\":{\"sunrise\":1740466458,\"sunset\":1740504790},\"wind\":{\"speed\":3.6}}");
        JSONObject weatherData2 = new JSONObject("{\"datetime\":1740487231,\"visibility\":8000," +
                "\"timezone\":0,\"weather\":{\"description\":\"Clear sky\",\"main\":\"Clear\"}," +
                "\"temperature\":{\"temp\":295.15,\"feels_like\":294.82},\"name\":\"Paris\"," +
                "\"sys\":{\"sunrise\":1740467458,\"sunset\":1740505790},\"wind\":{\"speed\":2.1}}");

        weatherClient.addCity("London");
        weatherClient.addCity("Paris");

        weatherClient.removeOldestCity();

        assertThrows(RuntimeException.class, () -> weatherClient.getWeather("London"));
        assertEquals(weatherData2, weatherClient.getWeather("Paris"));
    }
}


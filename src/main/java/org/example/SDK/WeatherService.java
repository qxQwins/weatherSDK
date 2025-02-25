package org.example.SDK;

import lombok.RequiredArgsConstructor;
import org.example.API.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Component
public class WeatherService {

    private ApiClient apiClient;

    @Autowired
    public WeatherService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public JSONObject restructureJSON(JSONObject JSONFromAPI) {
        JSONObject weatherObject = JSONFromAPI.getJSONArray("weather").getJSONObject(0);
        JSONObject mainObject = JSONFromAPI.getJSONObject("main");
        JSONObject windObject = JSONFromAPI.getJSONObject("wind");
        JSONObject sysObject = JSONFromAPI.getJSONObject("sys");

        JSONObject restructuredJSON = new JSONObject();

        restructuredJSON.put("weather", new JSONObject()
                .put("main", weatherObject.getString("main"))
                .put("description", weatherObject.getString("description")));

        restructuredJSON.put("temperature", new JSONObject()
                .put("temp", mainObject.get("temp"))
                .put("feels_like", mainObject.get("feels_like")));

        restructuredJSON.put("visibility", JSONFromAPI.getInt("visibility"));

        restructuredJSON.put("wind", new JSONObject()
                .put("speed", windObject.get("speed")));

        restructuredJSON.put("datetime", JSONFromAPI.getInt("dt"));

        restructuredJSON.put("sys", new JSONObject()
                .put("sunrise", sysObject.getInt("sunrise"))
                .put("sunset", sysObject.getInt("sunset")));

        restructuredJSON.put("timezone", JSONFromAPI.getInt("timezone"));

        restructuredJSON.put("name", JSONFromAPI.getString("name"));

        return restructuredJSON;
    }

    public JSONObject getWeatherData(String city) {
        JSONObject cityJSON = getLocationByName(city).getJSONObject(0);
        Object latitude = cityJSON.get("lat");
        Object longitude = cityJSON.get("lon");
        JSONObject weatherJSON = apiClient.JSONObjectParser
                ("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude +
                        "&appid=" + apiClient.getApiKey());

        weatherJSON = restructureJSON(weatherJSON);

        return weatherJSON;
    }

    public JSONArray getLocationByName(String city) {
        JSONArray locationJSON = apiClient.JSONArrayParser
                ("http://api.openweathermap.org/geo/1.0/direct?q=" + city +
                        "&appid=" + apiClient.getApiKey());
        return locationJSON;
    }

}

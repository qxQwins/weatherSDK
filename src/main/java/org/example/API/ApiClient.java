package org.example.API;

import lombok.Getter;
import org.example.exceptions.CityNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
@Getter
@Component
public class ApiClient {
    private final String apiKey;

    public ApiClient(@Value("${openweathermap.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    public String fetchJSON(String url) {
        StringBuilder resultJSON = new StringBuilder();
        try {
            URL locationUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) locationUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String output;
                while ((output = br.readLine()) != null) {
                    resultJSON.append(output);
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resultJSON.length() == 2)
            throw new CityNotFoundException("No such city");
        return resultJSON.toString();
    }

    public JSONObject JSONObjectParser(String url) {
        return new JSONObject(fetchJSON(url));
    }

    public JSONArray JSONArrayParser(String url) {
        return new JSONArray(fetchJSON(url));
    }
}

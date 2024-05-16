package org.webscraper.utils;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webscraper.client.Client;
import org.webscraper.client.GeoCodeClient;
import org.webscraper.model.Location;

import java.io.IOException;

public class GeoCodingUtil {
    private static final Client client = new GeoCodeClient(new OkHttpClient());
    private static final Logger logger = LoggerFactory.getLogger(GeoCodingUtil.class);

    public static Location getLocationFromAddress(String address) throws IOException {
        String formattedAddress = formatAddress(address);
        try (Response response = client.makeRequest(formattedAddress)) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            JSONArray results = new JSONArray(responseBody);

            if (results.length() > 0) {
                JSONObject location = results.getJSONObject(0);
                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lon");
                String countryCode = getCountryCode(location);
                return new Location(latitude, longitude, countryCode);
            }
            else {
                logger.info("No location found for this address {}", address);
                return null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getCountryCode(JSONObject location) {
        JSONObject address = location.optJSONObject("address");
        if (address != null) {
            return address.optString("country_code", "");
        }
        return "";
    }

    private static String formatAddress(String address) {
        // Remove any special characters or unnecessary details from the address
        String formattedAddress = address.replaceAll("[^a-zA-Z0-9\\s,]", "");
        // Replace spaces with "+" for URL encoding
        formattedAddress = formattedAddress.replace(" ", "+");
        return formattedAddress;
    }

}
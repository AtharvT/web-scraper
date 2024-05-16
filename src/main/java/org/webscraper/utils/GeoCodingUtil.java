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

/**
 * Utility class for geocoding addresses to geographical coordinates.
 * This class is currently not in use due to the inaccuracy of the latitude and longitude provided by the API.
 * However, it provides a scalable solution for future needs where only a location input is necessary to fetch relevant data.
 */
public class GeoCodingUtil {
    private static final Client client = new GeoCodeClient(new OkHttpClient());
    private static final Logger logger = LoggerFactory.getLogger(GeoCodingUtil.class);

    /**
     * Retrieves the geographical location from a given address.
     *
     * @param address the address to geocode
     * @return a Location object containing latitude, longitude, and country code; or null if no location is found
     * @throws IOException if there is a problem with the network or the server response
     */
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
            } else {
                logger.info("No location found for this address: {}", address);
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted during HTTP request", e);
        }
    }

    /**
     * Extracts the country code from the location JSON object.
     *
     * @param location the JSON object containing the location details
     * @return the country code if available; an empty string otherwise
     */
    private static String getCountryCode(JSONObject location) {
        JSONObject address = location.optJSONObject("address");
        return address != null ? address.optString("country_code", "") : "";
    }

    /**
     * Formats the given address for URL encoding by removing special characters and replacing spaces with '+'
     *
     * @param address the address to format
     * @return the formatted address suitable for HTTP requests
     */
    private static String formatAddress(String address) {
        return address.replaceAll("[^a-zA-Z0-9\\s,]", "").replace(" ", "+");
    }
}
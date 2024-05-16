package org.webscraper.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webscraper.model.Payload;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Client class for making geocoding requests to OpenStreetMap's Nominatim API.
 */
public class GeoCodeClient extends Client {
    private static final Logger logger = LoggerFactory.getLogger(GeoCodeClient.class);
    private static final String GEOCODING_API_URL = "https://nominatim.openstreetmap.org/search";

    /**
     * Constructs a GeoCodeClient with a specific HTTP client.
     *
     * @param httpClient the OkHttpClient instance to use for requests
     */
    public GeoCodeClient(OkHttpClient httpClient) {
        super(httpClient);
    }

    @Override
    public Response makeRequest(String address) throws IOException, InterruptedException {
        String url = GEOCODING_API_URL + "?q=" + address + "&format=json&limit=5&countrycodes=SG";
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (compatible; MyApp/1.0; +http://mywebsite.com)")
                .build();

        logger.info("Making request to geocoding API: {}", url);
        return httpClient.newCall(request).execute();
    }

    @Override
    public Response makeRequest(Payload payload) {
        // Implement if necessary, or remove if not used.
        return null;
    }
}


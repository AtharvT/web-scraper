package org.webscraper.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GeoCodeClient extends Client {
    private static final Logger logger = LoggerFactory.getLogger(GeoCodeClient.class);
    private static final String GEOCODING_API_URL = "https://nominatim.openstreetmap.org/search";

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
}

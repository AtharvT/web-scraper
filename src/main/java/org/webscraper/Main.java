package org.webscraper;

import okhttp3.OkHttpClient;
import org.webscraper.client.Client;
import org.webscraper.client.GrabApiClient;
import org.webscraper.exceptions.ScrapingException;
import org.webscraper.model.Restaurant;
import org.webscraper.service.MultiLocationScraper;
import org.webscraper.service.RestaurantScraper;
import org.webscraper.utils.FileUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> payloads = Arrays.asList(
                "{\"latlng\":\"1.396364,103.747462\",\"keyword\":\"\",\"offset\":0,\"pageSize\":32,\"countryCode\":\"SG\"}",  // Choa Chu Kang
                "{\"latlng\":\"1.367476,103.858326\",\"keyword\":\"\",\"offset\":0,\"pageSize\":32,\"countryCode\":\"SG\"}"  // Ang Mo Kio
        );

        List<String> locations = Arrays.asList(
                "Chong Boon Dental Surgery - Block 456 Ang Mo Kio Avenue 10, #01-1574,Singapore,560456",
                "PT Singapore - Choa Chu Kang North 6, Singapore, 689577"
        );

        OkHttpClient httpClient = new OkHttpClient();
        Client client = new GrabApiClient(httpClient);

        RestaurantScraper restaurantScraper = new RestaurantScraper(client);
        MultiLocationScraper multiLocationScraper = new MultiLocationScraper(client);

        Set<Restaurant> restaurants = new HashSet<>();

        try {
            restaurants = multiLocationScraper.scrapeLocations(locations, payloads);
        } catch (ScrapingException e) {
            throw new RuntimeException(e);
        }

        FileUtil.saveDataAsGzipNdjson(restaurants, "restaurants.ndjson.gz");
    }
}
package org.webscraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.webscraper.api.RestaurantScraper;
import org.webscraper.client.Client;
import org.webscraper.client.GrabApiClient;
import org.webscraper.exceptions.ScrapingException;
import org.webscraper.model.Payload;
import org.webscraper.model.Restaurant;
import org.webscraper.service.MultiLocationScrapingService;
import org.webscraper.utils.FileUtil;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Payload> payloads = Arrays.asList(
                new Payload("1.396364,103.747462", "", 0, 32, "SG"), // Choa Chu Kang
                new Payload("1.367476,103.858326", "", 0, 32, "SG")  // Ang Mo Kio
        );

        OkHttpClient httpClient = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        Client client = new GrabApiClient(httpClient, objectMapper);

        MultiLocationScrapingService multiLocationScrapingService = new MultiLocationScrapingService(client, objectMapper);
        RestaurantScraper restaurantScraper = new RestaurantScraper(multiLocationScrapingService, new HashMap<>());

        multiLocationScrapingService.startService(); // Start the scraping service
        Set<Restaurant> restaurants = new HashSet<>();

        try {
            restaurants = restaurantScraper.scrapeLocations(payloads);
        } catch (ScrapingException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed during scraping operations", e);
        } finally {
            try {
                multiLocationScrapingService.stopService(); // Ensure to stop the service even if an exception occurs
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Failed to stop the scraping service properly.");
            }
        }

        FileUtil.saveDataAsGzipNdjson(restaurants, "restaurants.ndjson.gz");
    }
}

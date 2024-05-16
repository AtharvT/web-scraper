package org.webscraper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kotlin.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webscraper.client.Client;
import org.webscraper.exceptions.ScrapingException;
import org.webscraper.model.MetaData;
import org.webscraper.model.Payload;
import org.webscraper.model.ScrapedData;
import java.util.*;
import java.util.concurrent.*;

/**
 * Service class to manage scraping operations across multiple locations concurrently.
 * This class now allows more controlled management of thread pool lifecycle and separates concerns more clearly.
 */
public class MultiLocationScrapingService {
    private static final int THREAD_COUNT = 10;
    private static final int SCRAPING_TIMEOUT_SECONDS = 30;
    private static final int RATE_LIMIT_DELAY_MILLIS = 1000;
    private static final Logger logger = LoggerFactory.getLogger(MultiLocationScrapingService.class);

    private final Client client;
    private final ObjectMapper objectMapper;
    private ExecutorService executorService;

    /**
     * Constructs a MultiLocationScrapingService with specified client and JSON mapper.
     *
     * @param client the client used for making HTTP requests
     * @param objectMapper the JSON mapper for processing data
     */
    public MultiLocationScrapingService(Client client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    /**
     * Initiates the thread pool, allowing for new tasks to be processed.
     */
    public void startService() {
        if (this.executorService.isShutdown() || this.executorService.isTerminated()) {
            this.executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        }
    }

    /**
     * Shuts down the thread pool gracefully, waiting for tasks to complete.
     */
    public void stopService() throws InterruptedException {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
            if (!executorService.awaitTermination(SCRAPING_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                logger.warn("Executor did not terminate in the allocated time, forcing shutdown...");
                executorService.shutdownNow();
            }
        }
    }

    /**
     * Initiates asynchronous scraping for a list of payloads.
     *
     * @param payloads list of payloads to be scraped
     * @return a list of CompletableFuture objects representing pending results of the scrape
     */
    public List<CompletableFuture<ScrapedData>> fetchScrapedData(List<Payload> payloads) {
        List<CompletableFuture<ScrapedData>> futures = new ArrayList<>();
        for (Payload payload : payloads) {
            CompletableFuture<ScrapedData> future = CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(RATE_LIMIT_DELAY_MILLIS); // Enforce rate limit before processing
                    RestaurantScrapingService scraper = new RestaurantScrapingService(client, objectMapper, new MetaData(payload.getLatlng()));
                    return scraper.scrape(payload);
                } catch (Exception e) {
                    throw new CompletionException(new ScrapingException("Error scraping payload: " + payload, e));
                }
            }, executorService);
            futures.add(future);
        }
        return futures;
    }

    /**
     * Processes the scraped data to extract and aggregate specific information.
     *
     * @param futures the list of CompletableFuture objects holding the scraped data
     * @return a map containing restaurant IDs mapped to their delivery fee and time
     */
    public Map<String, Pair<Double, Integer>> processScrapedData(List<CompletableFuture<ScrapedData>> futures) {
        Map<String, Pair<Double, Integer>> locationData = new HashMap<>();
        futures.forEach(future -> {
            try {
                ScrapedData scrapedData = future.join();
                if (scrapedData != null) {
                    scrapedData.restaurantSet().forEach(restaurant -> {
                        Double deliveryFee = restaurant.estimatedDeliveryFee().orElse(0.0);
                        Integer deliveryTime = restaurant.estimatedDeliveryTimeMinutes();
                        String restaurantId = restaurant.restaurantId();
                        locationData.put(restaurantId, new Pair<>(deliveryFee, deliveryTime));
                    });
                }
            } catch (CompletionException e) {
                logger.error("Error processing scraped data: " + e.getCause().getMessage(), e);
            }
        });
        return locationData;
    }

    /**
     * Generates and prints aggregate metadata for each location from the scraped data.
     *
     * @param futures the list of CompletableFuture objects holding the scraped data
     */
    public void generateAggregateMetaData(List<CompletableFuture<ScrapedData>> futures) {
        for (CompletableFuture<ScrapedData> future : futures) {
            ScrapedData scrapedData = future.join();
            System.out.println(" ");
            if (scrapedData != null) {
                MetaData metaData = scrapedData.metaData();
                System.out.println("MetaData for location: " + metaData.getLocation());
                System.out.println("Total Count: " + metaData.getTotalCount());
                System.out.println("Name - Null Count: " + metaData.getNameNullCount() + ", Not Null Count: " + metaData.getNameNotNullCount());
                System.out.println("Cuisine - Null Count: " + metaData.getCuisineNullCount() + ", Not Null Count: " + metaData.getCuisineNotNullCount());
                System.out.println("Rating - Null Count: " + metaData.getRatingNullCount() + ", Not Null Count: " + metaData.getRatingNotNullCount());
                System.out.println("Estimated Delivery Time - Null Count: " + metaData.getEstimatedDeliveryTimeNullCount() + ", Not Null Count: " + metaData.getEstimatedDeliveryTimeNotNullCount());
                System.out.println("Distance - Null Count: " + metaData.getDistanceNullCount() + ", Not Null Count: " + metaData.getDistanceNotNullCount());
                System.out.println("Is Promo Available - Null Count: " + metaData.getIsPromoAvailableNullCount() + ", Not Null Count: " + metaData.getIsPromoAvailableNotNullCount());
                System.out.println("Promo Description - Null Count: " + metaData.getPromoDescriptionNullCount() + ", Not Null Count: " + metaData.getPromoDescriptionNotNullCount());
                System.out.println("Image Link - Null Count: " + metaData.getImageLinkNullCount() + ", Not Null Count: " + metaData.getImageLinkNotNullCount());
                System.out.println("Restaurant ID - Null Count: " + metaData.getRestaurantIdNullCount() + ", Not Null Count: " + metaData.getRestaurantIdNotNullCount());
                System.out.println("Latitude - Null Count: " + metaData.getLatitudeNullCount() + ", Not Null Count: " + metaData.getLatitudeNotNullCount());
                System.out.println("Longitude - Null Count: " + metaData.getLongitudeNullCount() + ", Not Null Count: " + metaData.getLongitudeNotNullCount());
                System.out.println("Estimated Delivery Fee - Null Count: " + metaData.getEstimatedDeliveryFeeNullCount() + ", Not Null Count: " + metaData.getEstimatedDeliveryFeeNotNullCount());
                System.out.println("Closing Soon Text - Null Count: " + metaData.getClosingSoonTextNullCount() + ", Not Null Count: " + metaData.getClosingSoonTextNotNullCount());
            }
        }
    }
}
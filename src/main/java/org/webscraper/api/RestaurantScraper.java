package org.webscraper.api;

import kotlin.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webscraper.exceptions.ScrapingException;
import org.webscraper.model.Payload;
import org.webscraper.model.Restaurant;
import org.webscraper.model.ScrapedData;
import org.webscraper.service.MultiLocationScrapingService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class responsible for scraping restaurant data from multiple locations.
 * It leverages concurrency to handle multiple scraping tasks simultaneously.
 */
public class RestaurantScraper {
    private final MultiLocationScrapingService multiLocationScrapingService;
    private Map<String, Pair<Double, Integer>> restaurantIdFeeTimeMap;
    private static final Logger logger = LoggerFactory.getLogger(RestaurantScraper.class);

    /**
     * Constructs a RestaurantScraper with specified services and initial data map.
     *
     * @param multiLocationScrapingService the service used to scrape data from multiple locations
     * @param restaurantIdMap              the initial map of restaurant IDs to their corresponding fee and time
     */
    public RestaurantScraper(MultiLocationScrapingService multiLocationScrapingService, Map<String, Pair<Double, Integer>> restaurantIdMap) {
        this.multiLocationScrapingService = multiLocationScrapingService;
        this.restaurantIdFeeTimeMap = restaurantIdMap;
    }

    /**
     * Scrapes locations based on the provided payloads.
     *
     * @param payloads a list of payloads to scrape data from
     * @return a set of Restaurants with the scraped data
     * @throws ScrapingException    if scraping fails
     * @throws InterruptedException if the thread is interrupted
     */
    public Set<Restaurant> scrapeLocations(List<Payload> payloads) throws ScrapingException, InterruptedException {
        var futures = multiLocationScrapingService.fetchScrapedData(payloads);
        Set<Restaurant> restaurantSet = ConcurrentHashMap.newKeySet();  // Using a concurrent set for thread safety

        futures.forEach(future -> {
            try {
                ScrapedData scrapedData = future.join();  // Wait for the future to complete
                if (scrapedData != null) {
                    restaurantSet.addAll(scrapedData.restaurantSet());  // Add all restaurants from the scraped data
                }
            } catch (CompletionException e) {
                logger.error("Error processing future for scraped data: {}", e.getCause().getMessage(), e);
            }
        });

        this.restaurantIdFeeTimeMap = multiLocationScrapingService.processScrapedData(futures);
        multiLocationScrapingService.generateAggregateMetaData(futures);
        return restaurantSet;
    }

    /**
     * Displays the estimated fee and time for a given restaurant ID.
     *
     * @param restaurantId the ID of the restaurant
     */
    public void showEstimatedFeeTime(String restaurantId) {
        if (restaurantIdFeeTimeMap.containsKey(restaurantId)) {
            var pair = restaurantIdFeeTimeMap.get(restaurantId);
            logger.info("Estimated fee for restaurant id {} is {}", restaurantId, pair.getFirst());
            logger.info("Estimated time for restaurant id {} is {}", restaurantId, pair.getSecond());
        } else {
            throw new RuntimeException("The restaurantId is not present in our list");
        }
    }
}

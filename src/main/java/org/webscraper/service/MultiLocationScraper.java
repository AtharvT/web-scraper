package org.webscraper.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webscraper.client.Client;
import org.webscraper.exceptions.ScrapingException;
import org.webscraper.model.Restaurant;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MultiLocationScraper {
    private static final int THREAD_COUNT = 10;
    private static final int SCRAPING_TIMEOUT_SECONDS = 30;
    private static final int RATE_LIMIT_DELAY_MILLIS = 1000;
    private static final Logger logger = LoggerFactory.getLogger(MultiLocationScraper.class);

    private final Client client;
    private final ExecutorService executorService;

    public MultiLocationScraper(Client client) {
        this.client = client;
        this.executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    public Set<Restaurant> scrapeLocations(List<String> locations, List<String> payloads) throws ScrapingException {
        List<CompletableFuture<Set<Restaurant>>> futures = new ArrayList<>();

        for (String payload : payloads) {
            CompletableFuture<Set<Restaurant>> future = CompletableFuture.supplyAsync(() -> {
                RestaurantScraper scraper = new RestaurantScraper(client);
                try {
                    Set<Restaurant> restaurants = scraper.scrape(null, payload);
                    TimeUnit.MILLISECONDS.sleep(RATE_LIMIT_DELAY_MILLIS);
                    return restaurants;
                } catch (Exception e) {
                    throw new CompletionException(new ScrapingException("Error scraping location: " + payload, e));
                }
            }, executorService);

            futures.add(future);
        }

        try {
            Set<Restaurant> allRestaurants = futures.stream()
                    .map(future -> future.exceptionally(ex -> {
                        logger.error("Error scraping location", ex);
                        return new HashSet<>();
                    }))
                    .map(CompletableFuture::join)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            executorService.shutdown();
            executorService.awaitTermination(SCRAPING_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            return allRestaurants;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScrapingException("Scraping interrupted", e);
        }
    }
}
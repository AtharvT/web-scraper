package org.webscraper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webscraper.client.Client;
import org.webscraper.model.MetaData;
import org.webscraper.model.Payload;
import org.webscraper.model.Restaurant;
import org.webscraper.model.ScrapedData;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service for scraping restaurant data from external sources.
 */
public class RestaurantScrapingService {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantScrapingService.class);
    private static final int PAGE_SIZE = 26;
    private final ObjectMapper mapper;
    private final MetaData metaData;
    private final Client client;

    /**
     * Constructs a RestaurantScrapingService with the necessary dependencies.
     *
     * @param client       the client used for making HTTP requests
     * @param objectMapper the object mapper for JSON processing
     * @param metaData     the metadata object for recording data quality metrics
     */
    public RestaurantScrapingService(Client client, ObjectMapper objectMapper, MetaData metaData) {
        this.client = client;
        this.mapper = objectMapper;
        this.metaData = metaData;
    }

    /**
     * Performs scraping of restaurant data based on the provided payload.
     *
     * @param payload the data required for the scraping operation
     * @return a set of scraped restaurant data along with associated metadata
     * @throws IOException          if an I/O error occurs during request execution
     * @throws InterruptedException if the thread is interrupted during operation
     */
    public ScrapedData scrape(Payload payload) throws IOException, InterruptedException {
        Set<Restaurant> allRestaurants = new HashSet<>();
        int offset = 0;
        int count = 0;

        while (count < 250) {
            Payload updatedPayload = prepareExistingPayload(payload, offset);
            Response response = client.makeRequest(updatedPayload);

            if (response != null && response.isSuccessful()) {
                JsonNode rootNode = mapper.readTree(response.body().string());
                JsonNode restaurantsNode = rootNode.path("searchResult").path("searchMerchants");

                if (restaurantsNode.isArray()) {
                    for (JsonNode node : restaurantsNode) {
                        if (node.path("id").asText().startsWith("4-")) {
                            Restaurant restaurant = extractRestaurantData(node);
                            updateMetaData(restaurant);
                            allRestaurants.add(restaurant);
                            count++;
                        }
                    }
                    offset += PAGE_SIZE; // Move to the next page
                } else {
                    break; // No more pages available or no results returned
                }
            } else {
                logger.error("Failed to fetch data: {}", response);
                break;
            }
        }

        return new ScrapedData(allRestaurants, metaData);
    }

    private Payload prepareExistingPayload(Payload payload, int offset) {
        return new Payload(
                payload.getLatlng(),
                payload.getKeyword(),
                offset,
                PAGE_SIZE,
                payload.getCountryCode()
        );
    }

    private Restaurant extractRestaurantData(JsonNode node) {
        String name = node.path("address").path("name").asText();
        String cuisine = node.path("merchantBrief").path("cuisine").toString();
        double rating = node.path("merchantBrief").path("rating").asDouble();
        int estimatedDeliveryTime = node.path("estimatedDeliveryTime").asInt();
        double distance = node.path("merchantBrief").path("distanceInKm").asDouble();
        boolean isPromoAvailable = node.path("merchantBrief").path("promo").has("hasPromo");
        String promoDescription = node.path("merchantBrief").path("promo").path("description").asText();
        String imageLink = node.path("merchantBrief").path("photoHref").asText();
        String restaurantId = node.path("id").asText();
        double latitude = node.path("latlng").path("latitude").asDouble();
        double longitude = node.path("latlng").path("longitude").asDouble();

        Optional<String> closingSoonText = Optional.ofNullable(node.path("merchantBrief").path("closingSoonText").asText());
        if (closingSoonText.isEmpty()) {
            closingSoonText = Optional.empty();
        }
        Optional<Double> estimatedDeliveryFee = Optional.empty();
        String priceDisplay = node.path("estimatedDeliveryFee").path("priceDisplay").asText();
        if (!priceDisplay.isEmpty()) {
            try {
                double fee = Double.parseDouble(priceDisplay.replace("S$", ""));
                estimatedDeliveryFee = Optional.of(fee);
            } catch (NumberFormatException e) {
                logger.error("Error parsing estimated delivery fee: {}", priceDisplay, e);
            }
        }

        return new Restaurant(
                name,
                cuisine,
                rating,
                estimatedDeliveryTime,
                distance,
                isPromoAvailable,
                Optional.ofNullable(promoDescription),
                imageLink,
                restaurantId,
                latitude,
                longitude,
                estimatedDeliveryFee,
                closingSoonText
        );
    }

    private void updateMetaData(Restaurant restaurant) {
        metaData.incrementTotalCount();

        // Name
        if (restaurant.name() == null || restaurant.name().trim().isEmpty()) {
            metaData.incrementNullCount("name");
        } else {
            metaData.incrementNotNullCount("name");
        }

        // Cuisine
        if (restaurant.cuisine() == null || restaurant.cuisine().trim().isEmpty()) {
            metaData.incrementNullCount("cuisine");
        } else {
            metaData.incrementNotNullCount("cuisine");
        }

        // Rating
        metaData.incrementNotNullCount("rating"); // Since the rating has validation, it should never be null.

        // Estimated Delivery Time Minutes
        metaData.incrementNotNullCount("estimatedDeliveryTimeMinutes"); // Validated to be non-negative, so not null.

        // Distance
        metaData.incrementNotNullCount("distance"); // Validated to be non-negative, so not null.

        // Promo Availability
        metaData.incrementNotNullCount("isPromoAvailable"); // This is a primitive boolean, so it's never null.

        // Promo Description
        if (restaurant.promoDescription().isEmpty()) {
            metaData.incrementNullCount("promoDescription");
        } else {
            metaData.incrementNotNullCount("promoDescription");
        }

        // Image Link
        if (restaurant.imageLink() == null || restaurant.imageLink().trim().isEmpty()) {
            metaData.incrementNullCount("imageLink");
        } else {
            metaData.incrementNotNullCount("imageLink");
        }

        // Restaurant ID
        if (restaurant.restaurantId() == null || restaurant.restaurantId().trim().isEmpty()) {
            metaData.incrementNullCount("restaurantId");
        } else {
            metaData.incrementNotNullCount("restaurantId");
        }

        // Latitude
        metaData.incrementNotNullCount("latitude"); // Assuming always provided as per model constraints.

        // Longitude
        metaData.incrementNotNullCount("longitude"); // Assuming always provided as per model constraints.

        // Estimated Delivery Fee
        if (restaurant.estimatedDeliveryFee().isEmpty()) {
            metaData.incrementNullCount("estimatedDeliveryFee");
        } else {
            metaData.incrementNotNullCount("estimatedDeliveryFee");
        }

        // Closing Soon Text
        if (restaurant.closingSoonText().isEmpty()) {
            metaData.incrementNullCount("closingSoonText");
        } else {
            metaData.incrementNotNullCount("closingSoonText");
        }
    }

}
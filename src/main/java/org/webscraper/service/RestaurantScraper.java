package org.webscraper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webscraper.client.Client;
import org.webscraper.model.Location;
import org.webscraper.model.Restaurant;

import java.io.IOException;
import java.util.*;

public class RestaurantScraper {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantScraper.class);
    private static final int PAGE_SIZE = 26;

    private final Client client;

    public RestaurantScraper(Client client) {
        this.client = client;
    }

    public Set<Restaurant> scrape(String address, String existingPayload) throws IOException, InterruptedException {
//        Location location = GeoCodingUtil.getLocationFromAddress(address);
//        if (location == null) {
//            logger.error("Failed to geocode address: {}", address);
//            return Collections.emptySet();
//        }
//
//        String payload = preparePayload(location);
        Set<Restaurant> allRestaurants = new HashSet<>();
        int offset = 0;
        int count = 0;

        while (true) {
            Response response = client.makeRequest(existingPayload);

            if (response != null && response.isSuccessful()) {
                String responseBody = response.body().string();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(responseBody);
                JsonNode restaurantsNode = rootNode.path("searchResult").path("searchMerchants");

                if (restaurantsNode.isArray()) {
                    for (JsonNode node : restaurantsNode) {
                        String restaurantId = node.path("id").asText();
//                        String merchantStatus = node.path("merchantStatusInfo").get("status").asText();
//                        if(merchantStatus.startsWith("CLOSED") || "CLOSED_TEMPORATY_SCHEDULE_AVAILABLE".equals(merchantStatus) || "CLOSED_TEMPORATY_CLOSED".equals(merchantStatus) || "CLOSED".equals(merchantStatus)) {
//                            logger.info("status is unavailable");
//                            continue;
//                        }

                        if (restaurantId.startsWith("4-")) {
                            Restaurant restaurant = extractRestaurantData(node);
                            allRestaurants.add(restaurant);
                            count++;
                        }
                    }

                    if (count >= 250) {
                        break; // No more pages available
                    } else {
                        offset += PAGE_SIZE; // Move to the next page
//                        payload = preparePayload(location, offset);
                          existingPayload = preparePayloadFromExistingPayLoad(existingPayload, offset);
                    }
                } else {
                    break; // No results returned, end pagination
                }
            } else {
                logger.error("Failed to fetch data: {}", response);
                break;
            }
        }

        return allRestaurants;
    }

    private String preparePayload(Location location) {
        return preparePayload(location, 0);
    }

    private String preparePayload(Location location, int offset) {
        return String.format("{\"latlng\":\"%f,%f\",\"keyword\":\"\",\"offset\":%d,\"pageSize\":%d,\"countryCode\":\"%s\"}",
                location.latitude(), location.longitude(), offset, PAGE_SIZE, "SG");
    }

    private String preparePayloadFromExistingPayLoad(String payload, int offset) {
        try {
            JSONObject json = new JSONObject(payload);
            json.put("offset", offset);
            return json.toString();
        } catch (JSONException e) {
            logger.error("Error updating offset in payload", e);
            return payload;
        }
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
        logger.info("estimatedDeliveryFeeExtracted is {} for id {} ", node.path("estimatedDeliveryFee").path("priceDisplay").asText() , restaurantId);
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
}
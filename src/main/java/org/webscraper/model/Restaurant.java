package org.webscraper.model;

import java.util.Optional;

public record Restaurant(
        String name,
        String cuisine,
        double rating,
        int estimatedDeliveryTimeMinutes,
        double distance,
        boolean isPromoAvailable,
        Optional<String> promoDescription,
        String imageLink,
        String restaurantId,
        double latitude,
        double longitude,
        Optional<Double> estimatedDeliveryFee,
        Optional<String> closingSoonText
) {
    public Restaurant {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        if (estimatedDeliveryTimeMinutes < 0) {
            throw new IllegalArgumentException("Estimated delivery time must be non-negative");
        }
    }
}
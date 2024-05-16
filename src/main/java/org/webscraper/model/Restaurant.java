package org.webscraper.model;

import java.util.Optional;

/**
 * Record that encapsulates information about a restaurant.
 * It includes basic details such as name, cuisine, rating, and more,
 * with validation to ensure data integrity.
 */
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
    /**
     * Validates and constructs a new Restaurant object.
     *
     * @throws IllegalArgumentException if any parameter fails the validation checks.
     */
    public Restaurant {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant name cannot be null or empty");
        }
        if (cuisine == null || cuisine.trim().isEmpty()) {
            throw new IllegalArgumentException("Cuisine cannot be null or empty");
        }
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        if (estimatedDeliveryTimeMinutes < 0) {
            throw new IllegalArgumentException("Estimated delivery time must be non-negative");
        }
        if (distance < 0) {
            throw new IllegalArgumentException("Distance must be non-negative");
        }
        if (restaurantId == null || restaurantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant ID cannot be null or empty");
        }
    }
}

package org.webscraper.model;

/**
 * Class responsible for maintaining metadata counts for various attributes of restaurant listings.
 * This includes counts for null and non-null values for multiple fields.
 */
public class MetaData {
    private String location;
    private int totalCount;
    private int nameNullCount;
    private int nameNotNullCount;
    private int cuisineNullCount;
    private int cuisineNotNullCount;
    private int ratingNullCount;
    private int ratingNotNullCount;
    private int estimatedDeliveryTimeNullCount;
    private int estimatedDeliveryTimeNotNullCount;
    private int distanceNullCount;
    private int distanceNotNullCount;
    private int isPromoAvailableNullCount;
    private int isPromoAvailableNotNullCount;
    private int promoDescriptionNullCount;
    private int promoDescriptionNotNullCount;
    private int imageLinkNullCount;
    private int imageLinkNotNullCount;
    private int restaurantIdNullCount;
    private int restaurantIdNotNullCount;
    private int latitudeNullCount;
    private int latitudeNotNullCount;
    private int longitudeNullCount;
    private int longitudeNotNullCount;
    private int estimatedDeliveryFeeNullCount;
    private int estimatedDeliveryFeeNotNullCount;
    private int closingSoonTextNullCount;
    private int closingSoonTextNotNullCount;

    public MetaData(String location) {
        this.location = location;
    }


    public void incrementTotalCount() {
        totalCount++;
    }

    public void incrementNullCount(String field) {
        switch (field) {
            case "name" -> nameNullCount++;
            case "cuisine" -> cuisineNullCount++;
            case "rating" -> ratingNullCount++;
            case "estimatedDeliveryTimeMinutes" -> estimatedDeliveryTimeNullCount++;
            case "distance" -> distanceNullCount++;
            case "isPromoAvailable" -> isPromoAvailableNullCount++;
            case "promoDescription" -> promoDescriptionNullCount++;
            case "imageLink" -> imageLinkNullCount++;
            case "restaurantId" -> restaurantIdNullCount++;
            case "latitude" -> latitudeNullCount++;
            case "longitude" -> longitudeNullCount++;
            case "estimatedDeliveryFee" -> estimatedDeliveryFeeNullCount++;
            case "closingSoonText" -> closingSoonTextNullCount++;
        }
    }

    public void incrementNotNullCount(String field) {
        switch (field) {
            case "name" -> nameNotNullCount++;
            case "cuisine" -> cuisineNotNullCount++;
            case "rating" -> ratingNotNullCount++;
            case "estimatedDeliveryTimeMinutes" -> estimatedDeliveryTimeNotNullCount++;
            case "distance" -> distanceNotNullCount++;
            case "isPromoAvailable" -> isPromoAvailableNotNullCount++;
            case "promoDescription" -> promoDescriptionNotNullCount++;
            case "imageLink" -> imageLinkNotNullCount++;
            case "restaurantId" -> restaurantIdNotNullCount++;
            case "latitude" -> latitudeNotNullCount++;
            case "longitude" -> longitudeNotNullCount++;
            case "estimatedDeliveryFee" -> estimatedDeliveryFeeNotNullCount++;
            case "closingSoonText" -> closingSoonTextNotNullCount++;
        }
    }

    public String getLocation() {
        return location;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getNameNullCount() {
        return nameNullCount;
    }

    public int getNameNotNullCount() {
        return nameNotNullCount;
    }

    public int getCuisineNullCount() {
        return cuisineNullCount;
    }

    public int getCuisineNotNullCount() {
        return cuisineNotNullCount;
    }

    public int getRatingNullCount() {
        return ratingNullCount;
    }

    public int getRatingNotNullCount() {
        return ratingNotNullCount;
    }

    public int getEstimatedDeliveryTimeNullCount() {
        return estimatedDeliveryTimeNullCount;
    }

    public int getEstimatedDeliveryTimeNotNullCount() {
        return estimatedDeliveryTimeNotNullCount;
    }

    public int getDistanceNullCount() {
        return distanceNullCount;
    }

    public int getDistanceNotNullCount() {
        return distanceNotNullCount;
    }

    public int getIsPromoAvailableNullCount() {
        return isPromoAvailableNullCount;
    }

    public int getIsPromoAvailableNotNullCount() {
        return isPromoAvailableNotNullCount;
    }

    public int getPromoDescriptionNullCount() {
        return promoDescriptionNullCount;
    }

    public int getPromoDescriptionNotNullCount() {
        return promoDescriptionNotNullCount;
    }

    public int getImageLinkNullCount() {
        return imageLinkNullCount;
    }

    public int getImageLinkNotNullCount() {
        return imageLinkNotNullCount;
    }

    public int getRestaurantIdNullCount() {
        return restaurantIdNullCount;
    }

    public int getRestaurantIdNotNullCount() {
        return restaurantIdNotNullCount;
    }

    public int getLatitudeNullCount() {
        return latitudeNullCount;
    }

    public int getLatitudeNotNullCount() {
        return latitudeNotNullCount;
    }

    public int getLongitudeNullCount() {
        return longitudeNullCount;
    }

    public int getLongitudeNotNullCount() {
        return longitudeNotNullCount;
    }

    public int getEstimatedDeliveryFeeNullCount() {
        return estimatedDeliveryFeeNullCount;
    }

    public int getEstimatedDeliveryFeeNotNullCount() {
        return estimatedDeliveryFeeNotNullCount;
    }

    public int getClosingSoonTextNullCount() {
        return closingSoonTextNullCount;
    }

    public int getClosingSoonTextNotNullCount() {
        return closingSoonTextNotNullCount;
    }

    // Other methods for aggregation and reporting
}

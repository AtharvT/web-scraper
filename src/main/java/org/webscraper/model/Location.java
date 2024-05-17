package org.webscraper.model;

/**
 * A record representing a geographic location with latitude, longitude, and country code.
 * Records in Java provide a concise way to create immutable data objects.
 */
public record Location(double latitude, double longitude, String countryCode) {

    /**
     * Creates a new Location with specified latitude, longitude, and country code.
     *
     * @param latitude the geographic latitude
     * @param longitude the geographic longitude
     * @param countryCode the ISO country code representing the location's country
     */
}

